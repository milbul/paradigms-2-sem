tree(Key, Pr, Value, Left, Right).

%merge(empty, empty, empty) :- !.

merge(empty, Tree, Tree) :- !.

merge(Tree, empty, Tree) :- !.

merge(tree(Key1, Pr1, Value1, Left1, Right1), tree(Key2, Pr2, Value2, Left2, Right2), tree(Key1, Pr1, Value1, Left1, NewRight)) :-
  Pr1 > Pr2, !,
  merge(Right1, tree(Key2, Pr2, Value2, Left2, Right2), NewRight).

merge(tree(Key1, Pr1, Value1, Left1, Right1), tree(Key2, Pr2, Value2, Left2, Right2), tree(Key2, Pr2, Value2, NewLeft, Right2)) :-
  merge(tree(Key1, Pr1, Value1, Left1, Right1), Left2, NewLeft).

split(empty, NewKey, empty, empty) :- !.

split(tree(Key, Pr, Value, Left, Right), NewKey, tree(Key, Pr, Value, Left, Tree1), Tree2) :-
  Key < NewKey, !,
  split(Right, NewKey, Tree1, Tree2).

split(tree(Key, Pr, Value, Left, Right), NewKey, Tree1, tree(Key, Pr, Value, Tree2, Right)) :-
 split(Left, NewKey, Tree1, Tree2).


map_put(empty, NewKey, NewValue, tree(NewKey, NewPr, NewValue, empty, empty)) :-
 rand_int(1000000, NewPr), !.

map_put(tree(Key, Pr, Value, Left, Right), NewKey, NewValue, Res) :-
 rand_int(1000000, NewPr),
  split(tree(Key, Pr, Value, Left, Right), NewKey, LeftTree, RightTree),
  split(RightTree, NewKey + 1, UselessLeft, SuperRight),
  merge(LeftTree, tree(NewKey, NewPr, NewValue, empty, empty), TempTree),
  merge(TempTree, SuperRight, Res).

map_build(L, Res) :-
  map_build(L, empty, Res).

map_build([], TempTree, TempTree).

map_build([(Key, Value) | Tail], TempTree, Res) :-
  map_put(TempTree, Key, Value, NewTree),
  map_build(Tail, NewTree, Res).

map_remove(empty, DelKey, empty) :- !.

map_remove(tree(Key, Pr, Value, Left, Right), DelKey, Res) :-
  split(tree(Key, Pr, Value, Left, Right), DelKey, LeftTree, RightTree),
  split(RightTree, DelKey + 1, UselessLeft, SuperRight),
  merge(LeftTree, SuperRight, Res).

map_get(tree(NewKey, Pr, Value, Left, Right), NewKey, Value).

map_get(tree(Key, Pr, Value, Left, Right), NewKey, ResValue) :-
  Key > NewKey, !,
  map_get(Left, NewKey, ResValue).

map_get(tree(Key, Pr, Value, Left, Right), NewKey, ResValue) :-
  map_get(Right, NewKey, ResValue).

map_minKey(tree(Key, Pr, Value, empty, Right), Key) :- !.

map_minKey(tree(Key, Pr, Value, Left, Right), Res) :- 
 map_minKey(Left, Res).

map_maxKey(tree(Key, Pr, Value, Left, empty), Key) :- !.

map_maxKey(tree(Key, Pr, Value, Left, Right), Res) :- 
 map_maxKey(Right, Res).