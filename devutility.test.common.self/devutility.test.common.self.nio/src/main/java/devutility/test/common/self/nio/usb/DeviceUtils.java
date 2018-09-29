package devutility.test.common.self.nio.usb;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import devutility.internal.lang.ExceptionHelper;

public class DeviceUtils {
	public static DeviceList list() {
		DeviceList list = new DeviceList();
		int result = LibUsb.getDeviceList(null, list);

		if (result < 0) {
			throw new LibUsbException("Unable to get device list", result);
		}

		return list;
	}

	public static UsbDevice find(Context context, short vendorId, short productId) {
		int result = LibUsb.init(context);

		if (result < 0) {
			throw new LibUsbException("Unable to initialize libusb", result);
		}

		try {
			DeviceList devices = new DeviceList();
			result = LibUsb.getDeviceList(context, devices);

			if (result < 0) {
				throw new LibUsbException("Unable to get device list", result);
			}

			try {
				for (Device device : devices) {
					DeviceDescriptor descriptor = new DeviceDescriptor();
					result = LibUsb.getDeviceDescriptor(device, descriptor);

					if (result < 0) {
						LibUsbException exception = new LibUsbException("Unable to read device descriptor", result);
						System.out.println(ExceptionHelper.toString(exception));
						continue;
					}

					if (descriptor.idProduct() == productId && descriptor.idVendor() == vendorId) {
						UsbDevice usbDevice = new UsbDevice();
						usbDevice.setDevice(device);
						usbDevice.setDeviceDescriptor(descriptor);
						return usbDevice;
					}
				}
			} finally {
				LibUsb.freeDeviceList(devices, true);
			}
		} finally {
			LibUsb.exit(context);
		}

		return null;
	}
}