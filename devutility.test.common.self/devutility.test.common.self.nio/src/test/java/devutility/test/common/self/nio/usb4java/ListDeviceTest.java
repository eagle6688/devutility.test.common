package devutility.test.common.self.nio.usb4java;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import devutility.internal.test.BaseTest;
import devutility.internal.test.TestExecutor;

public class ListDeviceTest extends BaseTest {
	@Override
	public void run() {
		// Create the libusb context
		Context context = new Context();

		// Initialize the libusb context
		int result = LibUsb.init(context);

		if (result < 0) {
			throw new LibUsbException("Unable to initialize libusb", result);
		}

		// Read the USB device list
		DeviceList list = new DeviceList();
		result = LibUsb.getDeviceList(context, list);

		if (result < 0) {
			throw new LibUsbException("Unable to get device list", result);
		}

		try {
			// Iterate over all devices and list them
			for (Device device : list) {
				int busNumber = LibUsb.getBusNumber(device);
				int address = LibUsb.getDeviceAddress(device);

				DeviceDescriptor descriptor = new DeviceDescriptor();
				result = LibUsb.getDeviceDescriptor(device, descriptor);

				if (result < 0) {
					throw new LibUsbException("Unable to read device descriptor", result);
				}

				System.out.format("Bus %d, Device %d: Vendor %x, Product %x%n", busNumber, address, descriptor.idVendor(), descriptor.idProduct());
				System.out.format("Bus %03d, Device %03d: Vendor %04x, Product %04x%n", busNumber, address, descriptor.idVendor(), descriptor.idProduct());
				System.out.println(descriptor.toString());
			}
		} finally {
			// Ensure the allocated device list is freed
			LibUsb.freeDeviceList(list, true);
		}

		// Deinitialize the libusb context
		LibUsb.exit(context);
	}

	public static void main(String[] args) {
		TestExecutor.run(ListDeviceTest.class);
	}
}