"use strict";

const binary = (func => ((left, right) => ((x, y, z) => func(left(x, y, z), right(x, y, z)))));
const unary = (func => (val => ((x, y, z) => func(val(x, y, z)))));
const variable = (char => ((x, y, z) => char === 'x' ? x : char === 'y' ? y : z));
const cnst = (val => ((x, y, z) => val));

const add = binary((a, b) => a + b);
const subtract = binary((a, b) => a - b);
const multiply = binary((a, b) => a * b);
const divide = binary((a, b) => a / b);
const cos = unary(Math.cos);
const sin = unary(Math.sin);
const negate = unary((x) => -1 * x);
const pi = cnst(Math.PI);
const e = cnst(Math.E);

function parse(expression) {
    const binaryAr = ['-', '*', '/', '+'];
    const unaryAr = ['sin', 'cos', 'negate'];
    const variableAr = ['x', 'y', 'z'];
    let stack = [];
    const expressions = expression.split(' ').forEach(token => {
        if (binaryAr.includes(token)) {
            const right = stack.pop();
            const left = stack.pop();
            switch (token) {
                case '-' :
                    stack.push(subtract(left, right));
                    break;
                case '*' :
                    stack.push(multiply(left, right));
                    break;
                case '/' :
                    stack.push(divide(left, right));
                    break;
                case '+' :
                    stack.push(add(left, right));
                    break;
            }
        } else if (unaryAr.includes(token)) {
            const val = stack.pop();
            switch (token) {
                case 'sin' :
                    stack.push(sin(val));
                    break;
                case 'cos' :
                    stack.push(cos(val));
                    break;
                case 'negate' :
                    stack.push((negate(val)));
                    break;
            }
        } else if (variableAr.includes(token)) {
            stack.push(variable(token));
        } else if (token === 'pi') {
            stack.push(pi);
        }else if (token === 'e') {
            stack.push(e);
        }else if (Number(token)) {
            stack.push(cnst(parseInt(token)));
        }
    });
    return stack.pop();
}
