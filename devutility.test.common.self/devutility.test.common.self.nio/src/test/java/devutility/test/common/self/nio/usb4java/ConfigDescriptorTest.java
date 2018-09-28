package devutility.test.common.self.nio.usb4java;

import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import devutility.internal.test.TestExecutor;
import devutility.test.common.self.nio.BaseTest;

public class ConfigDescriptorTest extends BaseTest {
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
				System.out.println("=============================Start a new device=============================");
				DeviceDescriptor deviceDescriptor = new DeviceDescriptor();
				result = LibUsb.getDeviceDescriptor(device, deviceDescriptor);

				if (result < 0) {
					throw new LibUsbException("Unable to read device descriptor", result);
				}

				DeviceHandle deviceHandle = new DeviceHandle();
				result = LibUsb.open(device, deviceHandle);

				if (result != LibUsb.SUCCESS) {
					LibUsbException exception = new LibUsbException("Unable to open USB device", result);
					System.out.format("%s\n\n\n", exception.toString());
					continue;
				}

				showDeviceInfo(deviceHandle, deviceDescriptor);
				showConfigurationDescriptors(device, deviceDescriptor.bNumConfigurations(), deviceHandle);
				LibUsb.close(deviceHandle);
			}
		} finally {
			LibUsb.freeDeviceList(list, true);
			LibUsb.exit(context);
		}
	}

	private void showDeviceInfo(DeviceHandle deviceHandle, DeviceDescriptor deviceDescriptor) {
		showStringDescriptor("iManufacturer", deviceHandle, deviceDescriptor.iManufacturer());
		showStringDescriptor("iProduct", deviceHandle, deviceDescriptor.iProduct());
		System.out.format("bNumConfigurations: %d\n\n", deviceDescriptor.bNumConfigurations());
	}

	private void showConfigurationDescriptors(final Device device, final int numConfigurations, DeviceHandle deviceHandle) {
		for (byte i = 0; i < numConfigurations; i += 1) {
			final ConfigDescriptor descriptor = new ConfigDescriptor();
			final int result = LibUsb.getConfigDescriptor(device, i, descriptor);

			if (result < 0) {
				LibUsbException exception = new LibUsbException("Unable to read config descriptor", result);
				System.out.format("%s\n\n\n", exception.toString());
				continue;
			}

			try {
				System.out.println(descriptor.dump());
			} finally {
				LibUsb.freeConfigDescriptor(descriptor);
			}
		}
	}

	public static void main(String[] args) {
		TestExecutor.run(ConfigDescriptorTest.class);
	}
}