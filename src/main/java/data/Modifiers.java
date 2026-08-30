package data;

public class Modifiers {
	private boolean ctrl = false;
	private boolean shift = false;
	private boolean alt = false;
	private boolean meta = false;
	
	
	public boolean isCtrl() {
		return ctrl;
	}
	public void setCtrl(boolean ctrl) {
		this.ctrl = ctrl;
	}
	public boolean isShift() {
		return shift;
	}
	public void setShift(boolean shift) {
		this.shift = shift;
	}
	public boolean isAlt() {
		return alt;
	}
	public void setAlt(boolean alt) {
		this.alt = alt;
	}
	public boolean isMeta() {
		return meta;
	}
	public void setMeta(boolean meta) {
		this.meta = meta;
	}
}
