package devutility.test.common.self.nio;

import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;

public abstract class BaseTest extends devutility.internal.test.BaseTest {
	protected void showStringDescriptor(String name, final DeviceHandle handle, final byte index) {
		System.out.format("%s: %x, name: %s\n", name, index, LibUsb.getStringDescriptor(handle, index));
	}
}