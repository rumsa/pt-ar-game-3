var go_move=keyboard_check(vk_up)-keyboard_check(vk_down);
var go_turn=keyboard_check(vk_right)-keyboard_check(vk_left);
var boost=keyboard_check(vk_control);
var shoot=keyboard_check(vk_space);
var highlight=keyboard_check(ord("A"));

if (car.go_move != go_move || car.go_turn != go_turn || car.boost != boost || car.shoot != shoot || car.highlight != highlight) {
	car.go_move = go_move;
	car.go_turn = go_turn;
	car.boost = boost;
	car.shoot = shoot;
	car.highlight = highlight;
	scr_send_game_control(gameid, client_id, car.go_move, car.go_turn, car.boost, car.shoot, car.highlight);
}
