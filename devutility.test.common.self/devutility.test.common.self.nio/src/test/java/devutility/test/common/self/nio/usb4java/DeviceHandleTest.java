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
				System.out.format("idVendor: %d\n", deviceDescriptor.idVendor());

				//This method cannot convet decimal digit to HEX
				Integer idProduct = (int) deviceDescriptor.idProduct();
				System.out.format("idProduct: %d, HEX: %s\n", idProduct, Integer.toHexString(idProduct));

				DeviceHandle deviceHandle = new DeviceHandle();
				result = LibUsb.open(device, deviceHandle);

				if (result != LibUsb.SUCCESS) {
					LibUsbException exception = new LibUsbException("Unable to open USB device", result);
					System.out.format("%s\n\n\n", exception.toString());
					continue;
				}

				showStringDescriptor("bDescriptorType", deviceHandle, deviceDescriptor.bDescriptorType());
				showStringDescriptor("bDeviceClass", deviceHandle, deviceDescriptor.bDeviceClass());
				showStringDescriptor("bDeviceProtocol", deviceHandle, deviceDescriptor.bDeviceProtocol());
				showStringDescriptor("bDeviceSubClass", deviceHandle, deviceDescriptor.bDeviceSubClass());
				showStringDescriptor("bLength", deviceHandle, deviceDescriptor.bLength());
				showStringDescriptor("bMaxPacketSize0", deviceHandle, deviceDescriptor.bMaxPacketSize0());
				showStringDescriptor("bNumConfigurations", deviceHandle, deviceDescriptor.bNumConfigurations());
				showStringDescriptor("iManufacturer", deviceHandle, deviceDescriptor.iManufacturer());
				showStringDescriptor("iProduct", deviceHandle, deviceDescriptor.iProduct());
				showStringDescriptor("iSerialNumber", deviceHandle, deviceDescriptor.iSerialNumber());
				System.out.println("\n");
				LibUsb.close(deviceHandle);
			}
		} finally {
			LibUsb.freeDeviceList(list, true);
			LibUsb.exit(context);
		}
	}

	public static void main(String[] args) {
		TestExecutor.run(DeviceHandleTest.class);
	}
}