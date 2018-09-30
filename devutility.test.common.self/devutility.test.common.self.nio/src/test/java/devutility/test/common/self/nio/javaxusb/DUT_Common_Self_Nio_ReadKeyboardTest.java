package devutility.test.common.self.nio.javaxusb;

import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;
import javax.usb.util.UsbUtil;

import devutility.internal.test.TestExecutor;
import devutility.test.common.self.nio.BaseTest;

public class DUT_Common_Self_Nio_ReadKeyboardTest extends BaseTest {
	private static final short vendorId = 0x046d;
	private static final short productId = (short) 0xc326;
	private static final byte INTERFACE_AD = 0x00;
	private static final byte ENDPOINT_IN = (byte) 0x81;
	private static final byte ENDPOINT_OUT = (byte) 0x82;
	private static final byte[] COMMAND = { 0x01, 0x00 };

	@Override
	public void run() {
		UsbInterface iface;

		try {
			iface = readInit();
			listenData(iface);
			syncSend(iface, COMMAND);
		} catch (UsbException e) {
			e.printStackTrace();
		}
	}

	public static UsbInterface readInit() throws UsbException {
		UsbDevice device = findMissileLauncher(UsbHostManager.getUsbServices().getRootUsbHub());

		if (device == null) {
			System.out.println("Missile launcher not found.");
			System.exit(1);
		}

		UsbConfiguration configuration = device.getActiveUsbConfiguration();
		UsbInterface iface = configuration.getUsbInterface(INTERFACE_AD);
		iface.claim();
		return iface;
	}

	public static UsbDevice findMissileLauncher(UsbHub hub) {
		UsbDevice launcher = null;

		@SuppressWarnings("unchecked")
		List<UsbDevice> devices = (List<UsbDevice>) hub.getAttachedUsbDevices();

		for (UsbDevice device : devices) {
			if (device.isUsbHub()) {
				launcher = findMissileLauncher((UsbHub) device);

				if (launcher != null) {
					return launcher;
				}
			} else {
				UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();

				if (desc.idVendor() == vendorId && desc.idProduct() == productId) {
					return device;
				}
			}
		}

		return null;
	}

	public static Keyboardlistener listenData(UsbInterface usbInterface) {
		UsbEndpoint endpoint = usbInterface.getUsbEndpoint(ENDPOINT_IN);
		UsbPipe usbPipe = endpoint.getUsbPipe();
		Keyboardlistener listener = null;

		try {
			usbPipe.open();
			listener = new Keyboardlistener(usbPipe);

			Thread thread = new Thread(listener);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listener;
	}

	public static void syncSend(UsbInterface usbInterface, byte[] data) {
		UsbEndpoint endpoint = usbInterface.getUsbEndpoint(ENDPOINT_OUT);
		UsbPipe usbPipe = endpoint.getUsbPipe();

		try {
			if (!usbPipe.isOpen()) {
				usbPipe.open();
			}

			usbPipe.syncSubmit(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				usbPipe.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		TestExecutor.run(DUT_Common_Self_Nio_ReadKeyboardTest.class);
	}

	public static class Keyboardlistener implements Runnable {
		public boolean running = true;
		public UsbPipe usbPipe = null;

		public Keyboardlistener(UsbPipe usbPipe) {
			this.usbPipe = usbPipe;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[UsbUtil.unsignedInt(usbPipe.getUsbEndpoint().getUsbEndpointDescriptor().wMaxPacketSize())];

			while (running) {
				try {
					int transferredNumber = usbPipe.syncSubmit(buffer);

					if (transferredNumber > 0) {
						System.out.format("Have been transfered %d bytes.\n", transferredNumber);
					}
				} catch (UsbException exception) {
					if (running) {
						System.out.println("Unable to submit data buffer to HID mouse : " + exception.getMessage());
						break;
					}
				}

				if (running) {
					try {
						System.out.println(new String(buffer));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void stop() {
			running = false;
			usbPipe.abortAllSubmissions();
		}
	}
}