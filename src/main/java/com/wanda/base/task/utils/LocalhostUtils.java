package com.wanda.base.task.utils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * description:
 * 
 * @author senvon time : 2015年4月16日 下午5:15:35
 */
public class LocalhostUtils {
	/**
	 * 获得本机ip地址
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String[] getLocalIps(boolean ipv4, boolean ipv6, boolean loopback) {
		Enumeration netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		List<String> ips = new ArrayList<String>();
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = (NetworkInterface) netInterfaces.nextElement();
			Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				InetAddress inetAddress = inetAddresses.nextElement();
				if (inetAddress != null) {
					// ipv4地址
					if (ipv4 && inetAddress instanceof Inet4Address) {
						if (inetAddress.isLoopbackAddress()) {
							if (loopback) {
								ips.add(inetAddress.getHostAddress());
							}
						} else {
							ips.add(inetAddress.getHostAddress());
						}
					}
					// ipv6地址
					if (ipv6 && inetAddress instanceof Inet6Address) {
						if (inetAddress.isLoopbackAddress()) {
							if (loopback) {
								ips.add(inetAddress.getHostAddress());
							}
						} else {
							ips.add(inetAddress.getHostAddress());
						}
					}
				}
			}
		}
		return ips.toArray(new String[ips.size()]);
	}

	/**
	 * 获得本机所有ipv4地址，不包括loopback地址
	 * 
	 * @return 没有返回[]
	 */
	public static String[] getLocalIpv4s() {
		return getLocalIps(true, false, false);
	}

	/**
	 * 获得本机的一个ipv4地址，不包括loopback地址
	 * 
	 * @return
	 */
	public static String getLocalIpv4() {
		String[] localIpv4s = getLocalIpv4s();
		if (localIpv4s.length > 0) {
			return localIpv4s[0];
		}
		return null;
	}
}
