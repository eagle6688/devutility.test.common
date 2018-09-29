package devutility.test.common.self.nio.usb;

import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;

public class UsbDevice {
	private Device device;
	private DeviceDescriptor deviceDescriptor;

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public DeviceDescriptor getDeviceDescriptor() {
		return deviceDescriptor;
	}

	public void setDeviceDescriptor(DeviceDescriptor deviceDescriptor) {
		this.deviceDescriptor = deviceDescriptor;
	}
}