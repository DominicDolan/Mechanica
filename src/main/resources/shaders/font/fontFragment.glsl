#version 330

in vec2 pass_textureCoords;

out vec4 out_color;

uniform vec4 color;
uniform sampler2D fontAtlas;

const float edge = 0.5;
const float border_fraction = 0.2;

void main(void){
    float distance = 1.0 - texture(fontAtlas, pass_textureCoords).a;
    float alpha = (1.0 - smoothstep(edge, edge + border_fraction*edge, distance))*color.a;
	out_color = vec4(color.rgb, alpha);

//	out_color = vec4(0.5,0.5,0.5,1);
}