package com.lkyear.lllauncher.Torch;

public class TorchGlobals {

	// This is a singleton to keep track of whether the flashlight is on or off.

	static Boolean _isFlashOn = false;

	public static Boolean getIsFlashOn() {
		return _isFlashOn;
	}

	public static void setIsFlashOn(Boolean isFlashOn) {
		_isFlashOn = isFlashOn;
	}
}