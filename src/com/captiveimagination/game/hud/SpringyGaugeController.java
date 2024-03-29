/*
 * Copyright (c) 2007 shingoki
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'shingoki' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.captiveimagination.game.hud;

import com.captiveimagination.game.control.FloatSpring;
import com.jme.scene.Controller;

/**
 * Control a gauge to show a value from a bean, with
 * a springy link between bean value and displayed value,
 * to simulate a mechanical or sensor delay in the gauge.
 * @author goki
 */
public class SpringyGaugeController extends Controller {
	private static final long serialVersionUID = 8720962534725579344L;
	
	Gauge gauge;
	NamedValuesBean bean;
	String valueName;
	float factor;
	FloatSpring spring;

	/**
	 * Make a controller, which updates a specified gauge
	 * to show the named value of a bean
	 * This uses a default spring with a subsecond response time 
	 * for unit changes, suitable for most purposes, particularly
	 * for displaying values expected to take integer values which
	 * do not change at a very large rate per second (e.g. ammunition count)
	 * @param gauge
	 * 		The gauge to control
	 * @param bean
	 * 		The bean whose value will be displayed on the gauge
	 * @param valueName
	 * 		The name of the value of the bean to be displayed
	 * @param factor
	 * 		The factor by which to multiply the value before display
	 */
	public SpringyGaugeController(Gauge gauge, NamedValuesBean bean, String valueName, float factor) {
		this(gauge, bean, valueName, factor, new FloatSpring(100));
	}
	
	/**
	 * Make a controller, which updates a specified gauge
	 * to show the named value of a bean
	 * @param gauge
	 * 		The gauge to control
	 * @param bean
	 * 		The bean whose value will be displayed on the gauge
	 * @param valueName
	 * 		The name of the value of the bean to be displayed
	 * @param factor
	 * 		The factor by which to multiply the value before display
	 * @param spring
	 * 		The spring used to translate bean values into (delayed)
	 * 		gauge readout values.
	 */
	public SpringyGaugeController(Gauge gauge, NamedValuesBean bean, String valueName, float factor, FloatSpring spring) {
		super();
		this.gauge = gauge;
		this.bean = bean;
		this.valueName = valueName;
		this.factor = factor;
		this.spring = spring;
	}

	@Override
	public void update(float time) {
		float newValue = bean.getNamedValue(valueName) * factor;
		spring.update(newValue, time);
		gauge.setValue(spring.getPosition());
	}

}
