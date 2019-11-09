#version 400 core

uniform vec4 color;
uniform float strokeWidth;

in vec2 pass_textureCoords;

uniform sampler2D circleAtlas;

const float edge = 0.97;
uniform float border;

out vec4 out_Color;

void main(void){
	//out_Color = texture(textureSampler, pass_textureCoords);
    float innerEdge = edge - strokeWidth;
    float distance = 1.0 - texture(circleAtlas, pass_textureCoords).a;
    float alpha1 = (smoothstep(innerEdge, innerEdge + border, distance))*color.a;
    float alpha2 = (1.0 - smoothstep(edge, edge + border, distance))*color.a;
    float alpha = 0;
    if (strokeWidth >= 1.0){
        alpha = alpha2;
    } else {
        alpha = alpha1*alpha2;
    }

    out_Color = vec4(color.rgb, alpha);
//    out_Color = texture(circleAtlas, pass_textureCoords);
//	out_Color = pass_color;

}