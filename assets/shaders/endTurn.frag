#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
uniform float u_delay;
uniform float u_timeFactor;

varying vec4 v_color;
varying vec2 v_texCoord;


float cubicPulse( float c, float w, float x )
{
    x = abs(x - c);
    if( x>w ) return 0.0;
    x /= w;
    return 1.0 - x*x*(3.0-2.0*x);
}

void main() {

    vec4 image = texture2D(u_texture, v_texCoord);

    float percent = (u_time - u_delay) * u_timeFactor;

    image.a *= cubicPulse(percent, .1, v_texCoord.x);
//    image.r = percent;
//    float r = length(u_mouse.xy - p);
//
//    image.a *= smoothstep( 500. , 50., r);

    gl_FragColor = image * v_color;
}
