package com.github.richardflee.voyager.enums;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps text color to message type in Voyager log summary  
 */
public enum MatchersTypeEnum {
	TIMESTAMP(Color.LIGHT_GRAY), INFO(Color.GREEN), EVENT(Color.PINK), 
	WARNING(Color.YELLOW), EMERGENCY(Color.ORANGE), CRITICAL(Color.RED), 
	HIGHLIGHT(Color.WHITE), COMMENT(Color.LIGHT_GRAY), 
	METRIC_F(Color.CYAN), METRIC_G(Color.CYAN), METRIC_P(Color.CYAN);

	private Color color = null;
	private static final Map<String, MatchersTypeEnum> map = new HashMap<>();

	
	public boolean isWarning() {
		return this.equals(CRITICAL) || this.equals(EMERGENCY) || (this.equals(WARNING));
	}
	
	public boolean isMetric() {
		return this.equals(METRIC_F) || this.equals(METRIC_G) || (this.equals(METRIC_P));
	}
	
//	public boolean notWarning() {
//		return !this.isWarning();
//	}
	
	
	
	MatchersTypeEnum(Color color) {
		this.color = color;
	}

	// get color for this enum
	public Color getColor() {
		return this.color;
	}
	
	// class method, returns color for enum string value
	public static Color getColor(String strVal) {
		return getEnum(strVal).getColor();
	}

	public String getStrVal() {
		return this.toString();
	}

	// Returns enum for messageType input; invalid defaults to MessageTypesEnum.INFO.INFO if input is invalid
	public static MatchersTypeEnum getEnum(String messageType) {
		MatchersTypeEnum en = map.containsKey(messageType) ? map.get(messageType) : MatchersTypeEnum.INFO;
		return en;
	}

	// compiles map with key, value pairs comprising enum string value and enum value respectively
	static {
		for (final var en : MatchersTypeEnum.values()) {
			map.put(en.toString(), en);
		}
	}

	public static void main(String[] args) {

		System.out.println(String.format("Match INFO: %b", getEnum("INFO") == MatchersTypeEnum.INFO));
		System.out.println(String.format("Match EMERGENCY: %b", getEnum("EMERGENCY") == MatchersTypeEnum.EMERGENCY));

		System.out.println(String.format("INFO color = GREEN %b: ", MatchersTypeEnum.INFO.getColor()));

		for (var x : MatchersTypeEnum.values()) {
			System.out.println(x.toString());
		}
		
		var info_ = "INFO_";
		var x = getEnum(info_);
		System.out.println(x.toString());
		System.out.println(String.format("Invalid enum match defaults to INFO: %b",
				             getEnum("INFO_") == MatchersTypeEnum.INFO));
		
		System.out.println(String.format("String 'INFO' color GREEN: %b", MatchersTypeEnum.getColor("INFO")));
		System.out.println(String.format("String 'INFO_' color GREEN: %b", MatchersTypeEnum.getColor("INFO_")));
		
		System.out.println(String.format("\nIs WARNING: %s => %b", MatchersTypeEnum.WARNING.toString(), MatchersTypeEnum.WARNING.isWarning()));
		System.out.println(String.format("Is WARNING: %s => %b", MatchersTypeEnum.EMERGENCY.toString(), MatchersTypeEnum.EMERGENCY.isWarning()));
		System.out.println(String.format("Is WARNING: %s => %b", MatchersTypeEnum.CRITICAL.toString(), MatchersTypeEnum.CRITICAL.isWarning()));
		System.out.println(String.format("Is WARNING: %s => %b", MatchersTypeEnum.INFO.toString(), MatchersTypeEnum.INFO.isWarning()));
		
//		System.out.println(String.format("\nNot WARNING: %s => %b", MatchersTypeEnum.WARNING.toString(), MatchersTypeEnum.WARNING.notWarning()));
//		System.out.println(String.format("Not WARNING: %s => %b", MatchersTypeEnum.EMERGENCY.toString(), MatchersTypeEnum.EMERGENCY.notWarning()));
//		System.out.println(String.format("Not WARNING: %s => %b", MatchersTypeEnum.CRITICAL.toString(), MatchersTypeEnum.CRITICAL.notWarning()));
//		System.out.println(String.format("Not WARNING: %s => %b", MatchersTypeEnum.INFO.toString(), MatchersTypeEnum.INFO.notWarning()));
		
	}

}
