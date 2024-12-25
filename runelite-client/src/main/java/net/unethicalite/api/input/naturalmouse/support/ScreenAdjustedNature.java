package net.unethicalite.api.input.naturalmouse.support;

import net.unethicalite.api.input.naturalmouse.api.MouseInfoAccessor;
import net.unethicalite.api.input.naturalmouse.api.SystemCalls;

import java.awt.Dimension;
import java.awt.Point;

/**
 * This nature translates mouse coordinates to specified offset and screen dimension.
 * Internally it wraps the SystemCalls and MouseInfoAccessor in proxies which handle the translations.
 */
public class ScreenAdjustedNature extends DefaultMouseMotionNature
{
	private final Point offset;
	private final Dimension screenSize;

	public ScreenAdjustedNature(int x, int y, int x2, int y2)
	{
		this(new Dimension(x2 - x, y2 - y), new Point(x, y));
		if (y2 <= y || x2 <= x)
		{
			throw new IllegalArgumentException("Invalid range " + x + " " + y + " " + x2 + " " + y2);
		}
	}

	public ScreenAdjustedNature(Dimension screenSize, Point mouseOffset)
	{
		this.screenSize = screenSize;
		this.offset = mouseOffset;
	}

	@Override
	public void setMouseInfo(MouseInfoAccessor mouseInfo)
	{
		super.setMouseInfo(new ProxyMouseInfo(mouseInfo));
	}

	@Override
	public void setSystemCalls(SystemCalls systemCalls)
	{
		super.setSystemCalls(new ProxySystemCalls(systemCalls));
	}

	private class ProxyMouseInfo implements MouseInfoAccessor
	{
		private final MouseInfoAccessor underlying;
		// This implementation reuses the point.
		private final Point p = new Point();

		public ProxyMouseInfo(MouseInfoAccessor underlying)
		{
			this.underlying = underlying;
		}

		@Override
		public Point getMousePosition()
		{
			Point realPointer = underlying.getMousePosition();
			p.setLocation(realPointer.x - offset.x, realPointer.y - offset.y);
			return p;
		}
	}

	private class ProxySystemCalls implements SystemCalls
	{
		private final SystemCalls underlying;

		public ProxySystemCalls(SystemCalls underlying)
		{
			this.underlying = underlying;
		}

		@Override
		public long currentTimeMillis()
		{
			return underlying.currentTimeMillis();
		}

		@Override
		public void sleep(long time)
		{
			underlying.sleep(time);
		}

		@Override
		public Dimension getScreenSize()
		{
			return screenSize;
		}

		@Override
		public void setMousePosition(int x, int y)
		{
			underlying.setMousePosition(x + offset.x, y + offset.y);
		}
	}
}
