card(Color, Symbol, Number, Texture).

check_type(T1, T2, T3) :-
 T1 == T2,
 T2 == T3, !.

check_type(T1, T2, T3) :-
 T1 \== T2,
 T1 \== T3,
 T2 \== T3.

check_difference_pairs(card(Color1, Symbol1, Number1, Texture1), card(Color2, Symbol2, Number2, Texture2)) :-
 Color1 == Color2,
 Symbol1 == Symbol2,
 Number1 == Number2,
 Texture1 == Texture2.
 
check_difference(Card1, Card2, Card3) :-
 \+ check_difference_pairs(Card1, Card2),
 \+ check_difference_pairs(Card1, Card3),
 \+ check_difference_pairs(Card2, Card3).

check_all(card(Color1, Symbol1, Number1, Texture1), card(Color2, Symbol2, Number2, Texture2), card(Color3, Symbol3, Number3, Texture3)) :-
 check_type(Color1, Color2, Color3),
 check_type(Symbol1, Symbol2, Symbol3),
 check_type(Number1, Number2, Number3),
 check_type(Texture1, Texture2, Texture3).

check_input([]) :- !.
check_input([card(Color, Symbol, Number, Texture) | T]) :- 
 Colors = [red, green, purple],
 Symbols = [oval, squiggle, diamond],
 Numbers = [one, two, three],
 Textures = [solid, open, striped],
 member(Color, Colors),
 member(Symbol, Symbols), 
 member(Number, Numbers),
 member(Texture, Textures),
 check_input(T).


solve(L, R):- 
 check_input(L),
 findall(set(X, Y, Z), (member(X,L), member(Y,L), member(Z, L), check_all(X, Y, Z), check_difference(X, Y, Z)), R).

% ?- solve([card(red, oval, one, solid), card(red, oval, three, open), card(red, oval, two, striped)], R)