"use strict";

function Const(cnst) {
    this.value = cnst;
}
Const.prototype.evaluate = function () {
    return this.value;
};
Const.prototype.toString = function () {
    return this.value.toString();
};
Const.prototype.diff = function() {
    return ZERO;
};
Const.prototype.prefix = function () {
    return this.value.toString();
};
Const.prototype.postfix = function () {
    return this.value.toString();
};

const VARS = ["x", "y", "z"];


const ZERO = new Const(0);
const ONE = new Const(1);


function Variable(varName) {
    this.name = varName;
    this.ind = VARS.indexOf(varName);
}

Variable.prototype.evaluate = function (...args) {
    return args[this.ind];
};
Variable.prototype.toString = function () {
    return this.name;
};
Variable.prototype.diff = function (diffVar) {
    return this.name === diffVar ? ONE : ZERO;
};
Variable.prototype.prefix = function () {
    return this.name;
};
Variable.prototype.postfix = function () {
    return this.name;
};

function AbstractOp(args) {
    this.operands = args;
}

AbstractOp.prototype.evaluate = function (...args) {
    return this.opCalc(...this.operands.map(arg => arg.evaluate(...args)));
};
AbstractOp.prototype.toString = function () {
    return this.operands.join(" ") + " " + this.name;
};
AbstractOp.prototype.diff = function (diffVar) {
    let currArg = this.operands;
    return this.opDiff(...currArg.concat(currArg.map((arg) => arg.diff(diffVar))));
};
AbstractOp.prototype.prefix = function () {
    return '(' + this.name + ' ' + this.operands.map((arg) => arg.prefix()).join(' ') + ')';
};
AbstractOp.prototype.postfix = function () {
    let cur = this.operands.map((arg) => arg.postfix());
    return '(' + cur.join(' ') + ' ' + this.name + ')'
};

function makeOperation(calc, name, diff) {
    function Operation(...args) {
        AbstractOp.call(this, [...args]);
    }

    Operation.prototype = Object.create(AbstractOp.prototype);
    Operation.prototype.opCalc = calc;
    Operation.prototype.name = name;
    Operation.prototype.opDiff = diff;
    return Operation;
}


const Subtract = makeOperation(
    (x, y) => x - y,
    "-",
    (x, y, dx, dy) => new Subtract(dx, dy)
);
const Add = makeOperation(
    (x, y) => x + y,
    "+",
    (x, y, dx, dy) => new Add(dx, dy)
);
const Negate = makeOperation(
    (x) => -x,
    "negate",
    (x, dx) => new Negate(dx)
);
const Multiply = makeOperation(
    (x, y) => x * y,
    "*",
    function (x, y, dx, dy) {
        return new Add(
            new Multiply(dx, y),
            new Multiply(x, dy)
        );
    }
);
const Divide = makeOperation(
    (x, y) => x / y,
    "/",
    function (x, y, dx, dy) {
        return new Divide(
            new Subtract(
                new Multiply(dx, y),
                new Multiply(x, dy)
            ),
            new Multiply(y, y)
        );
    }
);
const Sinh = makeOperation(
    (x) => Math.sinh(x),
    "sinh",
    function(x, dx) {
        return new Multiply(
            dx,
            new Cosh(x));
    }
);
const Cosh = makeOperation(
    (x) => Math.cosh(x),
    "cosh",
    function(x, dx) {
        return new Multiply(
            dx,
            new Sinh(x));
    }
);

const Sum = makeOperation(
    (...args) => args.reduce((prev, val) => val + prev, 0),
    "sum",
    (x, ...args) => new Sum(...args.slice(args.length / 2)),
);

const Avg = makeOperation(
    (...args) => (args.reduce((prev, val) => val + prev, 0)) / args.length,
    "avg",
    (x, ...args) => new Avg(...args.slice(args.length / 2)),
);

const BINARY_OPERATIONS = {
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide,
};

const UNARY_OPERATIONS = {
    "sinh": Sinh,
    "cosh": Cosh,
    "negate": Negate,
};

function parse(expression) {
    return expression.split(' ').filter((token) => (token.length > 0)).reduce((stack, token) => {
        if (token in BINARY_OPERATIONS) {
            const right = stack.pop();
            const left = stack.pop();
            stack.push(new BINARY_OPERATIONS[token](left, right));
        } else if (token in UNARY_OPERATIONS) {
            stack.push(new UNARY_OPERATIONS[token](stack.pop()));
        } else if (VARS.includes(token)) {
            stack.push(new Variable(token));
        } else {
            stack.push(new Const(Number(token)));
        }
        return stack;
    }, []).pop();
}

const N_ARITY_OPERATIONS = {
    "sum": Sum,
    "avg": Avg,
};


function ParserError(message) {
    this.message = message;
}
ParserError.prototype = Object.create(Error.prototype);

function BracketError(type, str) {
    this.message = "Missing " + type + " in " + str;
}
BracketError.prototype = Object.create(ParserError.prototype);

function TokenError(token) {
    this.message = "Invalid token " + token;
}
TokenError.prototype = Object.create(ParserError.prototype);

function OperationError(type, str) {
    this.message = "Invalid " + type + str;
}
OperationError.prototype = Object.create(ParserError.prototype);


OperationError.prototype = Object.create(ParserError.prototype);

function EmptyError(message) {
    this.message = "Empty " + message;
}
EmptyError.prototype = Object.create(ParserError.prototype);

const parsePrefix = (expression) => parseAll(expression);
const parsePostfix =  (expression) => parseAll(expression);

const parseAll = function (expression) {
    let balance = 0;
    let pos = 0;
    let openBracketPos = 0;
    const skipWS = function () {
        while (pos < expression.length && expression[pos] === " ") {
            ++pos;
        }
    };
    const curParse = function () {
        let operations = [];
        let operands = [];
        if (expression.length === 0) {
            throw new EmptyError("input");
        }
        while (pos < expression.length) {
            skipWS();
            if (expression[pos] === '(') {
                openBracketPos = pos;
                ++balance;
                ++pos;
                operands.push(curParse());
            } else if (expression[pos] === ')') {
                if (operations.length > 1) {
                    throw new OperationError("operation ",expression.substring(openBracketPos, pos + 1));
                }
                if (operations.length === 0 && operands.length !== 0) {
                    for (let i = 0; i < operands.length; ++i) {
                        if (isNaN(Number(operands[i]))) {
                            throw new OperationError("variable op ",expression.substring(openBracketPos, pos + 1));
                        }
                    }
                    throw new OperationError("const op ",expression.substring(openBracketPos, pos + 1));
                }
                --balance;
                ++pos;
                break;
            } else {
                const op = getOp();
                if (VARS.includes(op)) {
                    operands.push(new Variable(op));
                } else if (op in UNARY_OPERATIONS || op in BINARY_OPERATIONS || op in N_ARITY_OPERATIONS) {
                    operations.push(op);
                    skipWS();
                } else {
                    if (isNaN(Number(op))) {
                        throw new TokenError(op);
                    }
                    operands.push(new Const(Number(op)));
                }
            }
            skipWS();
        }
        if (balance < 0) {
            throw new BracketError("(", expression);
        }
        if (operands.length === 1 && operations.length === 0) {
            return operands[0];
        }
        if (operations.length === 0) {
            if (operands.length === 0) {
                throw new EmptyError("operation");
            } else {
                throw new OperationError("operation ", expression);
            }
        }
        const op = operations[0];

        if (op in UNARY_OPERATIONS ) {
            if (operands.length !== 1) {
                throw new OperationError("unary ", expression);
            }

            return new UNARY_OPERATIONS[op](...operands);
        }
        if (op in BINARY_OPERATIONS) {
            if (operands.length !== 2) {
                throw new OperationError("binary " , expression);
            }

            return new BINARY_OPERATIONS[op](...operands);
        }

        return new N_ARITY_OPERATIONS[op](...operands);
    };
    const getOp = function () {
        const l = pos;
        skipWS();
        while (pos < expression.length && expression[pos] !== ' ' && expression[pos] !== ')' && expression[pos] !== '(') {
            pos++;
        }
        return expression.substring(l, pos);
    };
    const res = curParse();
    if (balance > 0) {
        throw new BracketError(")", expression);
    }
    return res;
};
//console.log(parsePostfix('(x 2 +)'));
//console.log(parsePostfix('(x   +  (y  z     * )  /) '));
//console.log(parsePostfix('  (  / (x negate  )  2   / )  '));
