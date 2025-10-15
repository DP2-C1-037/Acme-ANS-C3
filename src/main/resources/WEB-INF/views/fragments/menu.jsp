<%--
- menu.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:menu-bar>
	<acme:menu-left>
		<acme:menu-option code="master.menu.anonymous" access="isAnonymous()">
			<acme:menu-suboption code="master.menu.anonymous.favourite-link" action="http://www.example.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.adrian-favourite-link" action="https://es.wikipedia.org/wiki/Solanum_tuberosum"/>
			<acme:menu-suboption code="master.menu.anonymous.miguel-favourite-link" action="https://aitanamusic.es/"/>
			<acme:menu-suboption code="master.menu.anonymous.alejandro-favourite-link" action="https://www.marvel.com/"/>
			<acme:menu-suboption code="master.menu.anonymous.salma-favourite-link" action="https://www.reddit.com/"/>
      <acme:menu-suboption code="master.menu.anonymous.ignacio-favourite-link" action="https://github.com/UPocek/Car_AI?tab=readme-ov-file"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.administrator" access="hasRealm('Administrator')">
			<acme:menu-suboption code="master.menu.administrator.list-user-accounts" action="/administrator/user-account/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-airports" action="/administrator/airport/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-aircrafts" action="/administrator/aircraft/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-airlines" action="/administrator/airline/list"/>
			<acme:menu-suboption code="master.menu.administrator.list-published-bookings" action="/administrator/booking/list"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-initial" action="/administrator/system/populate-initial"/>
			<acme:menu-suboption code="master.menu.administrator.populate-db-sample" action="/administrator/system/populate-sample"/>			
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.administrator.shut-system-down" action="/administrator/system/shut-down"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.provider" access="hasRealm('Provider')">
			<acme:menu-suboption code="master.menu.provider.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>

		<acme:menu-option code="master.menu.consumer" access="hasRealm('Consumer')">
			<acme:menu-suboption code="master.menu.consumer.favourite-link" action="http://www.example.com/"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.customer" access="hasRealm('Customer')">
			<acme:menu-suboption code="master.menu.customer.bookings" action="/customer/booking/list"/>
			<acme:menu-suboption code="master.menu.customer.passengers" action="/customer/passenger/list"/>
			<acme:menu-suboption code="master.menu.customer.dashboard" action="/customer/customer-dashboard/show"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.flightCrewMember" access="hasRealm('FlightCrewMember')">
			<acme:menu-suboption code="master.menu.flightCrewMember.flightAssignmentCompleted" action="/flight-crew-member/flight-assignment/list-completed"/>
			<acme:menu-suboption code="master.menu.flightCrewMember.flightAssignmentMineCompleted" action="/flight-crew-member/flight-assignment/list-mine-completed"/>
			<acme:menu-suboption code="master.menu.flightCrewMember.flightAssignmentPlanned" action="/flight-crew-member/flight-assignment/list-planned"/>
			<acme:menu-suboption code="master.menu.flightCrewMember.flightAssignmentMinePlanned" action="/flight-crew-member/flight-assignment/list-mine-planned"/>
			<acme:menu-suboption code="master.menu.flightCrewMember.dashboard" action="/flight-crew-member/flight-crew-member-dashboard/show"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.any">
			<acme:menu-suboption code="master.menu.any.flightAssignment" action="/any/flight-assignment/list"/>
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.technician" access="hasRealm('Technician')">
			<acme:menu-suboption code="master.menu.technician.maintenance-record" action="/technician/maintenance-record/list"/>
			<acme:menu-suboption code="master.menu.technician.maintenance-record-mine" action="/technician/maintenance-record/list-mine"/>
			<acme:menu-separator/>
			<acme:menu-suboption code="master.menu.technician.task" action="/technician/task/list"/>
			<acme:menu-suboption code="master.menu.technician.task-mine" action="/technician/task/list-mine"/>
		</acme:menu-option>
	
		<acme:menu-option code="master.menu.airlineManager" access="hasRealm('AirlineManager')">
			<acme:menu-suboption code="master.menu.airlineManager.flight" action="/airline-manager/flight/list"/>
			
		</acme:menu-option>
		
		<acme:menu-option code="master.menu.assistance-agent" access="hasRealm('AssistanceAgent')">
			<acme:menu-suboption code="master.menu.assistance-agent.claim.list-completed" action="/assistance-agent/claim/list-completed"/>
			<acme:menu-suboption code="master.menu.assistance-agent.claim.list-undergoing" action="/assistance-agent/claim/list-undergoing"/>
			<acme:menu-suboption code="master.menu.assistance-agent.dashboard" action="/assistance-agent/assistance-agent-dashboard/show"/>
			
		</acme:menu-option>
	
	</acme:menu-left>

	<acme:menu-right>		
		<acme:menu-option code="master.menu.user-account" access="isAuthenticated()">
			<acme:menu-suboption code="master.menu.user-account.general-profile" action="/authenticated/user-account/update"/>
			<acme:menu-suboption code="master.menu.user-account.become-provider" action="/authenticated/provider/create" access="!hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.provider-profile" action="/authenticated/provider/update" access="hasRealm('Provider')"/>
			<acme:menu-suboption code="master.menu.user-account.become-consumer" action="/authenticated/consumer/create" access="!hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.consumer-profile" action="/authenticated/consumer/update" access="hasRealm('Consumer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-customer" action="/authenticated/customer/create" access="!hasRealm('Customer')"/>
			<acme:menu-suboption code="master.menu.user-account.become-flight-crew-member" action="/authenticated/flight-crew-member/create" access="!hasRealm('FlightCrewMember')"/>
			<acme:menu-suboption code="master.menu.user-account.customer-profile" action="/authenticated/customer/update" access="hasRealm('Customer')"/>
			<acme:menu-suboption code="master.menu.user-account.flight-crew-member-profile" action="/authenticated/flight-crew-member/update" access="hasRealm('FlightCrewMember')"/>
		</acme:menu-option>
	</acme:menu-right>
</acme:menu-bar>

