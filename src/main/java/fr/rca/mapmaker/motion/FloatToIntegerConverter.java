package fr.rca.mapmaker.motion;

import org.jdesktop.beansbinding.Converter;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FloatToIntegerConverter extends Converter<Float, Integer> {

	@Override
	public Integer convertForward(Float t) {
		if(t != null) {
			return t.intValue();
		} else {
			return null;
		}
	}
	
	@Override
	public Float convertReverse(Integer s) {
		if(s != null) {
			return s.floatValue();
		} else {
			return null;
		}
	}

}
