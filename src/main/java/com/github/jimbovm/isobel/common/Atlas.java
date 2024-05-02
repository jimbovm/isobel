package com.github.jimbovm.isobel.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.ToString;

/**
 * A container for instances of {@link Area} which maintains the same ordering
 * of areas by environment as in the game itself and which generates an index
 * number for each area it contains. It can be accessed either by immutable name
 * to obtain a {@link Area}, or by {@link Area} to obtain an index number.
 */
@Getter
@ToString
@XmlRootElement(name = "atlas")
public final class Atlas {

	@XmlElement(name = "area")
	private List<Area> areas;

	/** Immutable names mapped to areas. */
	private Map<String, Area> areasById;

	/** Areas mapped to index numbers. */
	private Map<Area, Integer> indexByArea;

	/** Counts of each area type, used to generate offsets. */
	private Map<Area.Environment, Integer> areaCounts;

	{
		this.areas = new ArrayList<>();
		this.areasById = new HashMap<>();
		this.indexByArea = new HashMap<>();
		this.areaCounts = new HashMap<>();

		for (Area.Environment environment : Area.Environment.values()) {
			areaCounts.put(environment, 0);
		}
	}

	private void regenerateIndexByArea() {
		Map<Area, Integer> map = new HashMap<>();

		int subindex = 0;

		for (Area.Environment environment : Area.Environment.values()) {

			// reset pointer for this environment type
			subindex = 0;

			// grab all areas having this environment
			List<Area> environmentAreas = this.areas.stream()
				.filter((area) -> area.getEnvironment() == environment)
				.collect(Collectors.toList());

			// assign each one a subindex sequentially and map them to it
			for (Area area : environmentAreas) {
				int indexNumber = (environment.getId() << 5) | subindex;
				map.put(area, indexNumber);
				subindex += 1;
			}
		}

		this.indexByArea = map;
	}

	private void regenerateAreaById() {

		Map<String, Area> areasById = new HashMap<>();

		for (Area area : this.areas) {
			areasById.put(area.getId(), area);
		}

		this.areasById = areasById;
	}

	private void regenerateAreaCounts() {
		
		Map<Area.Environment, Integer> counts = this.areas.stream().collect(Collectors.groupingBy(Area::getEnvironment, Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

		this.areaCounts = counts;
	}

	/** Add an area to the atlas.
	 *  @param area The area to add.
	 */
	public void add(Area area) {
		if (this.areas.contains(area)) {
			throw new IllegalStateException(String.format("Area %s already in atlas", area.getId()));
		}
		this.areas.add(area);
		Collections.sort(this.areas, Comparator.comparing(Area::getEnvironment));
		this.areasById.put(area.getId(), area);
		this.regenerateIndexByArea();
		int currentCount = this.areaCounts.get(area.getEnvironment());
		this.areaCounts.put(area.getEnvironment(), currentCount + 1);
	}

	/** Add a collection of areas to the atlas.
	 *  @param areas A collection of areas. 
	 */
	public void addAll(Collection<Area> areas) {
		for (Area area : areas) {
			if (this.areas.contains(area)) {
				throw new IllegalStateException(String.format("Area %s already in atlas", area.getId()));
			}
		}
		for (Area area : areas) {
			this.areas.add(area);
			this.areasById.put(area.getId(), area);
			int currentCount = this.areaCounts.get(area.getEnvironment());
			this.areaCounts.put(area.getEnvironment(), currentCount + 1);
		}
		Collections.sort(this.areas, Comparator.comparing(Area::getEnvironment));
		this.regenerateIndexByArea();
	}

	/**
	 * Remove an area from the atlas.
	 * 
	 * @param area The area to be removed.
	 */
	public void remove(Area area) {
		this.areas.remove(area);
		this.areasById.remove(area.getId());
		this.indexByArea.remove(area);
		this.regenerateIndexByArea();
		int currentCount = this.areaCounts.get(area.getEnvironment());
		this.areaCounts.put(area.getEnvironment(), currentCount - 1);
	}

	/**
	 * Get an area from the atlas.
	 * 
	 * @param id The ID of the desired area.
	 * @return The area with the supplied ID.
	 */
	public Area get(String id) {
		return this.areasById.get(id);
	}

	/**
	 * Get the index number of an area.
	 * 
	 * @param area The area whose index number is desired.
	 * @return The index number of the supplied area.
	 */
	public int getIndex(Area area) {
		return this.indexByArea.get(area);
	}

	/**
	 * Executed by JAXB following unmarshalling. Regenerates atlas state
	 * not explicitly serialized.
	 * 
	 * @param unmarshaller The unmarshaller being used.
	 * @param parent The parent object.
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		this.regenerateIndexByArea();
		this.regenerateAreaById();
		this.regenerateAreaCounts();
	}
}