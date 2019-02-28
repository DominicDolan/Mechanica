#version 140
in vec3 position;

uniform vec2 resinv;
out vec2 pass_Resinv;
out vec2 v_rgbNW;
out vec2 v_rgbNE;
out vec2 v_rgbSW;
out vec2 v_rgbSE;
out vec2 v_rgbM;
out vec2 textureCoords;
varying vec2 vUv;


void main(void) {
    gl_Position = vec4(position, 1.0);
    pass_Resinv = resinv;

    //compute the texture coords and send them to varyings
    textureCoords = (position.xy + 1.0) * 0.5;

//    vec2 inverseVP = 1.0 / resinv.xy;
    v_rgbNW = textureCoords + vec2(-resinv.x, -resinv.y);
    v_rgbNE = textureCoords + vec2( resinv.x, -resinv.y);
    v_rgbSW = textureCoords + vec2(-resinv.x,  resinv.y);
    v_rgbSE = textureCoords + vec2( resinv.x,  resinv.y);
    v_rgbM  = vec2(textureCoords);

}