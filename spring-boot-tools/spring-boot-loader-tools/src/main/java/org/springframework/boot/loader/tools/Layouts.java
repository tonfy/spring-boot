/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.loader.tools;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Common {@link Layout}s.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author David Turanski
 */
public class Layouts {

	public static final String JAR = "JAR";

	public static final String NONE = "NONE";

	public static final String EXPANDED = "EXPANDED";

	public static final String WAR = "WAR";

	private static final Map<String, String> registeredLayouts = new HashMap<String, String>();

	static {
		registerLayout(JAR, Jar.class);
		registerLayout(WAR, War.class);
		registerLayout(NONE, None.class);
		registerLayout(EXPANDED, Expanded.class);
	}

	/**
	 * Return the a layout for the given source file.
	 *
	 * @param file the source file
	 * @return a {@link Layout}
	 */
	public static Layout forFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("File must not be null");
		}
		if (file.getName().toLowerCase().endsWith(".jar")) {
			return new Jar();
		}
		if (file.getName().toLowerCase().endsWith(".war")) {
			return new War();
		}
		if (file.isDirectory() || file.getName().toLowerCase().endsWith(".zip")) {
			return new Expanded();
		}
		throw new IllegalStateException("Unable to deduce layout for '" + file + "'");
	}

	public static Layout forName(String layoutName) {
		String className = registeredLayouts.get(layoutName.toUpperCase());
		Assert.notNull(className,"Unknown layout - " + layoutName);
		Layout layout = null;
		try {
			layout = (Layout) Class.forName(className).newInstance();
		}
		catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return layout;
	}

	public static void registerLayout(String layoutName, Class<? extends Layout> layoutClass) {
		registeredLayouts.put(layoutName, layoutClass.getName());
	}

	/**
	 * Executable JAR layout.
	 */
	public static class Jar implements Layout {

		@Override
		public String getLauncherClassName() {
			return "org.springframework.boot.loader.JarLauncher";
		}

		@Override
		public String getLibraryDestination(String libraryName, LibraryScope scope) {
			return "lib/";
		}

		@Override
		public String getClassesLocation() {
			return "";
		}
	}

	/**
	 * Executable expanded archive layout.
	 */
	public static class Expanded extends Jar {

		@Override
		public String getLauncherClassName() {
			return "org.springframework.boot.loader.PropertiesLauncher";
		}

	}

	/**
	 * Executable expanded archive layout.
	 */
	public static class None extends Jar {

		@Override
		public String getLauncherClassName() {
			return null;
		}
	}

	/**
	 * Executable WAR layout.
	 */
	public static class War implements Layout {

		private static final Map<LibraryScope, String> SCOPE_DESTINATIONS;

		static {
			Map<LibraryScope, String> map = new HashMap<LibraryScope, String>();
			map.put(LibraryScope.COMPILE, "WEB-INF/lib/");
			map.put(LibraryScope.RUNTIME, "WEB-INF/lib/");
			map.put(LibraryScope.PROVIDED, "WEB-INF/lib-provided/");
			SCOPE_DESTINATIONS = Collections.unmodifiableMap(map);
		}

		@Override
		public String getLauncherClassName() {
			return "org.springframework.boot.loader.WarLauncher";
		}

		@Override
		public String getLibraryDestination(String libraryName, LibraryScope scope) {
			return SCOPE_DESTINATIONS.get(scope);
		}

		@Override
		public String getClassesLocation() {
			return "WEB-INF/classes/";
		}
	}

}
