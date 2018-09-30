package devutility.test.common.self.nio.javaxusb;

import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbConst;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbNotClaimedException;
import javax.usb.UsbNotOpenException;
import javax.usb.UsbPipe;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;

import devutility.internal.test.TestExecutor;
import devutility.test.common.self.nio.BaseTest;

public class DUT_Common_Self_Nio_KeyboardTest extends BaseTest {
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

		currentConfigurationNumber(usbDevice);
		System.out.format("Usb device config state: %b\n", usbDevice.isConfigured());
		System.out.format("Usb device isUsbHub state: %b\n", usbDevice.isUsbHub());

		UsbConfiguration configuration = usbDevice.getActiveUsbConfiguration();
		System.out.format("Usb interface count: %d\n", configuration.getUsbInterfaces().size());

		UsbInterface iface = configuration.getUsbInterface((byte) 1);

		try {
			iface.claim();
		} catch (UsbNotActiveException | UsbDisconnectedException | UsbException e) {
			e.printStackTrace();
		}

		try {
			UsbEndpoint endpoint = iface.getUsbEndpoint((byte) 0x82);

			if (endpoint == null) {
				throw new RuntimeException("Endpoint not found!");
			}

			UsbPipe pipe = endpoint.getUsbPipe();

			try {
				pipe.open();
			} catch (UsbNotActiveException | UsbNotClaimedException | UsbDisconnectedException | UsbException e) {
				e.printStackTrace();
			}

			try {
				pipe.addUsbPipeListener(new UsbPipeListener() {
					@Override
					public void errorEventOccurred(UsbPipeErrorEvent event) {
						System.out.println(event);
					}

					@Override
					public void dataEventOccurred(UsbPipeDataEvent event) {
						System.out.println(new String(event.getData()));
					}
				});

				byte[] data = new byte[8];
				int received = pipe.syncSubmit(data);
				System.out.println(received + " bytes received");
			} catch (UsbNotActiveException | UsbNotOpenException | IllegalArgumentException | UsbDisconnectedException | UsbException e) {
				e.printStackTrace();
			} finally {
				try {
					pipe.close();
				} catch (UsbNotActiveException | UsbNotOpenException | UsbDisconnectedException | UsbException e) {
					e.printStackTrace();
				}
			}
		} finally {
			try {
				iface.release();
			} catch (UsbNotActiveException | UsbDisconnectedException | UsbException e) {
				e.printStackTrace();
			}
		}
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

	void currentConfigurationNumber(UsbDevice usbDevice) {
		UsbControlIrp irp = usbDevice.createUsbControlIrp((byte) (UsbConst.REQUESTTYPE_DIRECTION_IN | UsbConst.REQUESTTYPE_TYPE_STANDARD | UsbConst.REQUESTTYPE_RECIPIENT_DEVICE), UsbConst.REQUEST_GET_CONFIGURATION,
				(short) 0, (short) 0);

		irp.setData(new byte[1]);

		try {
			usbDevice.syncSubmit(irp);
			System.out.format("Current configuration number: %d\n", irp.getData()[0]);
		} catch (IllegalArgumentException | UsbDisconnectedException | UsbException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TestExecutor.run(DUT_Common_Self_Nio_KeyboardTest.class);
	}
}