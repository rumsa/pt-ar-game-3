/*if (display_aa > 12) {
	display_reset(8, true);
}*/

// setup cars on the grid
red1 = instance_create_layer(100, 250, "car", obj_car_with_physics);
with (red1) {
	image_blend = make_color_rgb(240,128,128);
	phy_rotation = 0;
}

red2 = instance_create_layer(100, 570, "car", obj_car_with_physics);
with (red2) {
	image_blend = make_color_rgb(205,92,92);
	phy_rotation = 0;
}

blue1 = instance_create_layer(1340, 250, "car", obj_car_with_physics);
with (blue1) {
	image_blend = make_color_rgb(66, 134, 244);
	phy_rotation = 182.5;
}

blue2 = instance_create_layer(1340, 570, "car", obj_car_with_physics);
with (blue2) {
	image_blend = make_color_rgb(46, 108, 209);
	phy_rotation = 182.5;
	dodraw = true;
}

client_map=ds_map_create();