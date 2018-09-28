package devutility.test.common.self.nio.usb4java;

import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.DescriptorUtils;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.Interface;
import org.usb4java.InterfaceDescriptor;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import devutility.internal.test.TestExecutor;
import devutility.test.common.self.nio.BaseTest;

public class InterfaceTest extends BaseTest {
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
				configurationDescriptors(device, deviceDescriptor.bNumConfigurations(), deviceHandle);
				LibUsb.close(deviceHandle);
			}
		} finally {
			LibUsb.freeDeviceList(list, true);
			LibUsb.exit(context);
		}
	}

	private void showDeviceInfo(DeviceHandle deviceHandle, DeviceDescriptor descriptor) {
		showStringDescriptor("iManufacturer", deviceHandle, descriptor.iManufacturer());
		showStringDescriptor("iProduct", deviceHandle, descriptor.iProduct());
		System.out.println("");
	}

	private void configurationDescriptors(final Device device, final int numConfigurations, DeviceHandle deviceHandle) {
		for (byte i = 0; i < numConfigurations; i += 1) {
			final ConfigDescriptor descriptor = new ConfigDescriptor();
			final int result = LibUsb.getConfigDescriptor(device, i, descriptor);

			if (result < 0) {
				LibUsbException exception = new LibUsbException("Unable to read config descriptor", result);
				System.out.format("%s\n\n\n", exception.toString());
				continue;
			}

			try {
				interfaces(descriptor.iface());
			} finally {
				LibUsb.freeConfigDescriptor(descriptor);
			}
		}
	}

	private void interfaces(Interface[] interfaces) {
		System.out.format("Count of interfaces: %d\n", interfaces.length);

		for (Interface iface : interfaces) {
			interfaceDescriptors(iface.altsetting());
		}
	}

	private void interfaceDescriptors(InterfaceDescriptor[] descriptors) {
		System.out.format("Count of InterfaceDescriptor array: %d\n", descriptors.length);

		for (InterfaceDescriptor descriptor : descriptors) {
			System.out.println("Interface Descriptor:");
			System.out.format("bLength %18d%n", descriptor.bLength());
			System.out.format("bDescriptorType %10d%n", descriptor.bDescriptorType());
			System.out.format("bInterfaceNumber %9d%n", descriptor.bInterfaceNumber());
			System.out.format("bInterfaceNumber %9d\n", toHexInt(descriptor.bInterfaceNumber()));
			System.out.format("bAlternateSetting %8d%n", descriptor.bAlternateSetting());
			System.out.format("bNumEndpoints %12d%n", descriptor.bNumEndpoints());
			System.out.format("bInterfaceClass %10d %s%n", descriptor.bInterfaceClass(), DescriptorUtils.getUSBClassName(descriptor.bInterfaceClass()));
			System.out.format("bInterfaceSubClass %7d%n", descriptor.bInterfaceSubClass());
			System.out.format("bInterfaceProtocol %7d%n", descriptor.bInterfaceProtocol());
			System.out.format("iInterface %15d%n", descriptor.iInterface());
		}
	}

	public static void main(String[] args) {
		TestExecutor.run(InterfaceTest.class);
	}
}