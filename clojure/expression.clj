(defn operation [f]
  (fn [& operands]
    (fn [args] (apply f ((apply juxt operands) args))))
  )

(defn constant [val] (constantly val))
(defn variable [name] (fn [args] (get args name)))

(def add (operation +))
(def subtract (operation -))
(def multiply (operation *))
(def divide (operation (fn [& args] (reduce (fn [a b] (/ (double a) (double b))) (first args) (rest args)))))
(def negate subtract)
(def min (operation #'clojure.core/min))
(def max (operation #'clojure.core/max))

(def operations {'+ add '- subtract '* multiply '/ divide 'negate negate 'min min 'max max})

(declare Constant)
(declare Variable)
(declare moreOperations)

(defn parseAll [mode]
  (fn [expression]
    (letfn [(curParse [token]
              (cond
                (number? token) (if (== mode 1) (Constant token) (constant token))
                (symbol? token) (if (== mode 1) (Variable (str token)) (variable (str token)))
                (list? token) (apply (if (== mode 1) (get moreOperations (first token)) (get operations (first token))) (mapv curParse (rest token)))))
            ]
      (curParse (read-string expression)))))


(definterface IExpression
  (toString [])
  (evaluate [mp])
  (diff [diffName]))

(declare ZERO)
(declare ONE)

(deftype ConstantConstr [num]
  IExpression
  (toString [_] (format "%.1f" num))
  (evaluate [_ _] num)
  (diff [_ _] ZERO))

(defn Constant [num] (ConstantConstr. num))

(def ZERO (Constant 0))
(def ONE (Constant 1))

(deftype VariableConstr [name]
  IExpression
  (toString [_] (str name))
  (evaluate [_ mp] (get mp name))
  (diff [_ diffName] (if (= name diffName) ONE ZERO)))

(defn Variable [name] (VariableConstr. name))

(defn evaluate [expression mp] (.evaluate expression mp))
(defn toString [expression] (.toString expression))
(defn diff [expression name] (.diff expression name))


(deftype Expression [args name function howDiff]
  IExpression
  (toString [_] (str "(" name " " (clojure.string/join " " (mapv toString args)) ")"))
  (evaluate [_ mp] (apply function (mapv (fn [x] (evaluate x mp)) args)))
  (diff [_ diffName] (howDiff args (mapv (fn [x] (diff x diffName)) args))))

(defn Add [& args] (Expression. args "+" +
                                (fn [_ d_args] (apply Add d_args))))

(defn Subtract [& args] (Expression. args "-" -
                                     (fn [_ d_args] (apply Subtract d_args))))

(defn Multiply [& args] (Expression. args "*" *
                                     (fn [args d_args]
                                       (second (reduce
                                                 (fn [[x dx] [y dy]] [(Multiply x y)
                                                                      (Add
                                                                        (Multiply dx y)
                                                                        (Multiply x dy))])
                                                 (mapv vector args d_args))))))

(defn Divide [& args] (Expression. args "/" (fn [x y] (/ x (double y))) ;??
                                   (fn [[x y] [dx dy]]
                                     (Divide
                                       (Subtract
                                         (Multiply y dx)
                                         (Multiply x dy))
                                       (Multiply y y)))))

(defn Negate [& args] (Expression. args "negate" -
                                   (fn [_ dx] (Negate (first dx)))))

(defn Lg [& args] ( Expression. args "lg" (fn [x y] (/ (Math/log (Math/abs y)) (Math/log (Math/abs x))))
                                (fn [[x y] [dx dy]] (Subtract
                                                  (Divide
                                                    dy
                                                    (Multiply y (Lg (Constant Math/E) x)))
                                                  (Divide
                                                    (Multiply dx (Lg x y))
                                                    (Multiply x (Lg (Constant Math/E) x)))))))

(defn Pw [& args] (Expression. args "pw" (fn [x y] (Math/pow x y))
                               (fn [[x y] [dx dy]]
                                 (Multiply
                                   (Pw x (Subtract y ONE))
                                   (Add
                                     (Multiply y dx)
                                     (Multiply x
                                       (Multiply
                                         (Lg (Constant Math/E) x) dy)))))))


(def moreOperations {'+ Add '- Subtract '* Multiply '/ Divide 'negate Negate 'pw Pw 'lg Lg})

(def parseFunction (parseAll 0))
(def parseObject (parseAll 1))
