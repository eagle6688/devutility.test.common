package devutility.test.common.self.nio.usb4java;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import devutility.internal.test.TestExecutor;
import devutility.test.common.self.nio.BaseTest;

public class DeviceHandleTest extends BaseTest {
	@Override
	public void run() {
		Context context = new Context();
		int result = LibUsb.init(context);

		if (result < 0) {
			throw new LibUsbException("Unable to initialize libusb", result);
		}

		DeviceList list = new DeviceList();
		result = LibUsb.getDeviceList(context, list);

		if (result < 0) {
			throw new LibUsbException("Unable to get device list", result);
		}

		try {
			for (Device device : list) {
				DeviceDescriptor deviceDescriptor = new DeviceDescriptor();
				result = LibUsb.getDeviceDescriptor(device, deviceDescriptor);

				if (result < 0) {
					throw new LibUsbException("Unable to read device descriptor", result);
				}

				System.out.print(deviceDescriptor.dump());
				DeviceHandle deviceHandle = new DeviceHandle();
				result = LibUsb.open(device, deviceHandle);

				if (result != LibUsb.SUCCESS) {
					LibUsbException exception = new LibUsbException("Unable to open USB device", result);
					System.out.format("%s\n\n\n", exception.toString());
					continue;
				}

				showStringDescriptor(deviceHandle, deviceDescriptor.bDescriptorType(), "bDescriptorType");
				showStringDescriptor(deviceHandle, deviceDescriptor.bDeviceClass(), "bDeviceClass");
				showStringDescriptor(deviceHandle, deviceDescriptor.bDeviceProtocol(), "bDeviceProtocol");
				showStringDescriptor(deviceHandle, deviceDescriptor.bDeviceSubClass(), "bDeviceSubClass");
				showStringDescriptor(deviceHandle, deviceDescriptor.bLength(), "bLength");
				showStringDescriptor(deviceHandle, deviceDescriptor.bMaxPacketSize0(), "bMaxPacketSize0");
				showStringDescriptor(deviceHandle, deviceDescriptor.bNumConfigurations(), "bNumConfigurations");
				showStringDescriptor(deviceHandle, deviceDescriptor.iManufacturer(), "iManufacturer");
				showStringDescriptor(deviceHandle, deviceDescriptor.iProduct(), "iProduct");
				showStringDescriptor(deviceHandle, deviceDescriptor.iSerialNumber(), "iSerialNumber");
				System.out.println("\n");
				LibUsb.close(deviceHandle);
			}
		} finally {
			LibUsb.freeDeviceList(list, true);
		}
	}

	private void showStringDescriptor(final DeviceHandle handle, final byte index, String name) {
		System.out.format("%s: %x, name: %s\n", name, index, LibUsb.getStringDescriptor(handle, index));
	}

	public static void main(String[] args) {
		TestExecutor.run(DeviceHandleTest.class);
	}
}