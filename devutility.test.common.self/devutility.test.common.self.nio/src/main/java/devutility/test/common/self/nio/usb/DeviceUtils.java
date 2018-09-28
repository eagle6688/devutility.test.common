package devutility.test.common.self.nio.usb;

import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

public class DeviceUtils {
	public static DeviceList list() {
		DeviceList list = new DeviceList();
		int result = LibUsb.getDeviceList(null, list);

		if (result < 0) {
			throw new LibUsbException("Unable to get device list", result);
		}

		return list;
	}
}