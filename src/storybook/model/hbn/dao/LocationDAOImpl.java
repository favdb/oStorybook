/*
Storybook: Open Source software for novelists and authors.
Copyright (C) 2008 - 2012 Martin Mustun, 2015 FaVdB

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package storybook.model.hbn.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Person;
import storybook.toolkit.DateUtil;

public class LocationDAOImpl extends SbGenericDAOImpl<Location, Long> implements
		LocationDAO {

	public LocationDAOImpl() {
		super();
	}

	public LocationDAOImpl(Session session) {
		super(session);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Location> findAll() {
		Query query = session.createQuery("from Location order by location_id,country,city,name");
		return (List<Location>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> findCountries(){
		//TODO order by l.country
		Query query = session.createQuery("select distinct(l.country) from Location as l order by l.country");
		return (List<String>)query.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> findCities(){
		//TODO order by l.city
		Query query = session.createQuery("select distinct(l.city) from Location as l");
		return (List<String>)query.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> findCitiesByCountry(String country) {
		if (country == null) {
			Query query = session.createQuery("select distinct(l.city) from Location as l where l.country is null order by l.city");
			return (List<String>) query.list();
		}
		Query query = session.createQuery("select distinct(l.city) from Location as l where l.country=:country order by l.city");
		query.setParameter("country", country);
		return (List<String>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Location> findByCountries(List<String> countries) {
		if (countries.isEmpty()) {
			return new ArrayList<Location>();
		}
		Query query = session.createQuery("from Location as l where l.country in (:countries)");
		query.setParameterList("countries", countries);
		return (List<Location>) query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Location> findByCountry(String country) {
		Criteria crit = session.createCriteria(Location.class);
		if (country == null) {
			crit.add(Restrictions.isNull("country"));
		} else {
			crit.add(Restrictions.eq("country", country));
		}
		List<Location> locations = (List<Location>) crit.list();
		return locations;
	}

	@SuppressWarnings("unchecked")
	public List<Location> findByCity(String city) {
		Criteria crit = session.createCriteria(Location.class);
		if (city == null) {
			crit.add(Restrictions.isNull("city"));
		} else {
			crit.add(Restrictions.eq("city", city));
		}
		List<Location> locations = (List<Location>) crit.list();
		return locations;
	}

	@SuppressWarnings("unchecked")
	public List<Location> findByCountryCity(String country, String city) {
		Criteria crit = session.createCriteria(Location.class);
		if (country == null) {
			crit.add(Restrictions.isNull("country"));
		} else {
			crit.add(Restrictions.eq("country", country));
		}
		if (city == null) {
			crit.add(Restrictions.isNull("city"));
		} else {
			crit.add(Restrictions.eq("city", city));
		}
		crit.addOrder(Order.asc("name"));
		List<Location> locations = (List<Location>) crit.list();
		return locations;
	}

	public long countByPersonLocationDate(Person person, Location location, Date date){
		date = DateUtil.getZeroTimeDate(date);
		Query query = session.createQuery(
				  "select count(s) from Scene as s" +
				  " join s.persons as p" +
				  " join s.locations as l" +
				  " where p=:person and l=:location"
				  +" and s.sceneTs between :tsStart and :tsEnd");
		query.setEntity("person", person);
		query.setEntity("location", location);
		Timestamp tsStart = new Timestamp(date.getTime());
		date = DateUtils.addDays(date, 1);
		date = DateUtils.addMilliseconds(date, -1);
		Timestamp tsEnd = new Timestamp(date.getTime());
		query.setTimestamp("tsStart", tsStart);
		query.setTimestamp("tsEnd", tsEnd);
		return (Long)query.uniqueResult();
	}
}
