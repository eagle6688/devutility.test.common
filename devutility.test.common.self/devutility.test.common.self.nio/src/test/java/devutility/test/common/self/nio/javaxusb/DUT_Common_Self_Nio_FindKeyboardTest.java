package devutility.test.common.self.nio.javaxusb;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;

import devutility.internal.test.TestExecutor;
import devutility.test.common.self.nio.BaseTest;

public class DUT_Common_Self_Nio_FindKeyboardTest extends BaseTest {
	private static final short vendorId = 0x046d;
	private static final short productId = (short) 0xc326;

	@Override
	public void run() {
		UsbHub usbHub = null;

		try {
			usbHub = UsbHostManager.getUsbServices().getRootUsbHub();
		} catch (SecurityException | UsbException e) {
			e.printStackTrace();
		}

		UsbDevice usbDevice = findDevice(usbHub, vendorId, productId);

		if (usbDevice == null) {
			System.out.format("UsbDevice with vendorId %x productId %x not found!\n", vendorId, productId);
			return;
		}

		try {
			System.out.format("ManufacturerString: %s\n", usbDevice.getManufacturerString());
		} catch (UnsupportedEncodingException | UsbDisconnectedException | UsbException e) {
			e.printStackTrace();
		}

		System.out.format("Usb device config state: %b\n", usbDevice.isConfigured());
		System.out.format("Usb device isUsbHub state: %b\n", usbDevice.isUsbHub());
	}

	public UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
		@SuppressWarnings("unchecked")
		List<UsbDevice> usbDevices = (List<UsbDevice>) hub.getAttachedUsbDevices();

		for (UsbDevice device : usbDevices) {
			UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();

			if (desc.idVendor() == vendorId && desc.idProduct() == productId) {
				return device;
			}

			if (device.isUsbHub()) {
				device = findDevice((UsbHub) device, vendorId, productId);

				if (device != null) {
					return device;
				}
			}
		}

		return null;
	}

	public static void main(String[] args) {
		TestExecutor.run(DUT_Common_Self_Nio_FindKeyboardTest.class);
	}
}