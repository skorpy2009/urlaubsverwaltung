package org.synyx.urlaubsverwaltung.application.service;

import junit.framework.Assert;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.synyx.urlaubsverwaltung.account.Account;
import org.synyx.urlaubsverwaltung.account.HolidaysAccountService;
import org.synyx.urlaubsverwaltung.application.dao.ApplicationDAO;
import org.synyx.urlaubsverwaltung.application.domain.Application;
import org.synyx.urlaubsverwaltung.application.domain.ApplicationStatus;
import org.synyx.urlaubsverwaltung.application.domain.DayLength;
import org.synyx.urlaubsverwaltung.application.domain.VacationType;
import org.synyx.urlaubsverwaltung.calendar.JollydayCalendar;
import org.synyx.urlaubsverwaltung.calendar.OwnCalendarService;
import org.synyx.urlaubsverwaltung.person.Person;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Unit test for {@link CalculationService}.
 *
 * @author  Aljona Murygina - murygina@synyx.de
 */
public class CalculationServiceTest {

    private CalculationService service;
    private ApplicationDAO applicationDAO;
    private HolidaysAccountService accountService;
    private OwnCalendarService calendarService;

    @Before
    public void setUp() {

        applicationDAO = Mockito.mock(ApplicationDAO.class);
        accountService = Mockito.mock(HolidaysAccountService.class);
        calendarService = new OwnCalendarService(new JollydayCalendar());

        service = new CalculationService(applicationDAO, accountService, calendarService);
    }


    /**
     * Test of checkApplication method, of class CalculationService.
     */
    @Test
    public void testCheckApplication() {

        Person person = new Person();
        person.setLoginName("horscht");

        DateMidnight firstMilestone = new DateMidnight(2012, DateTimeConstants.JANUARY, 1);
        DateMidnight lastMilestone = new DateMidnight(2012, DateTimeConstants.MARCH, 31);

        Application a1 = new Application();
        a1.setStartDate(new DateMidnight(2011, DateTimeConstants.DECEMBER, 29));
        a1.setEndDate(new DateMidnight(2012, DateTimeConstants.JANUARY, 3));
        a1.setHowLong(DayLength.FULL);
        // must be 4 days at all: 2 before January + 2 after January

        Application a2 = new Application();
        a2.setStartDate(new DateMidnight(2012, DateTimeConstants.MARCH, 12));
        a2.setEndDate(new DateMidnight(2012, DateTimeConstants.MARCH, 16));
        a2.setHowLong(DayLength.FULL);
        a2.setDays(BigDecimal.valueOf(5));

        Application a3 = new Application();
        a3.setStartDate(new DateMidnight(2012, DateTimeConstants.FEBRUARY, 6));
        a3.setEndDate(new DateMidnight(2012, DateTimeConstants.FEBRUARY, 9));
        a3.setHowLong(DayLength.FULL);
        a3.setDays(BigDecimal.valueOf(4));

        Application a4 = new Application();
        a4.setStartDate(new DateMidnight(2012, DateTimeConstants.MARCH, 29));
        a4.setEndDate(new DateMidnight(2012, DateTimeConstants.APRIL, 5));
        a4.setHowLong(DayLength.FULL);
        // must be 6 days at all: 2 before April + 4 after April

        Mockito.when(applicationDAO.getApplicationsBetweenTwoMilestones(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a2, a3));

        Mockito.when(applicationDAO.getApplicationsBeforeFirstMilestone(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a1));

        Mockito.when(applicationDAO.getApplicationsAfterLastMilestone(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a4));

        DateMidnight firstMilestone2 = new DateMidnight(2012, DateTimeConstants.APRIL, 1);
        DateMidnight lastMilestone2 = new DateMidnight(2012, DateTimeConstants.DECEMBER, 31);

        Application b1 = new Application();
        b1.setStartDate(new DateMidnight(2012, DateTimeConstants.DECEMBER, 27));
        b1.setEndDate(new DateMidnight(2013, DateTimeConstants.JANUARY, 3));
        b1.setHowLong(DayLength.FULL);
        // must be 4 days at all: 2.5 before January + 2 after January

        Application b2 = new Application();
        b2.setStartDate(new DateMidnight(2012, DateTimeConstants.SEPTEMBER, 3));
        b2.setEndDate(new DateMidnight(2012, DateTimeConstants.SEPTEMBER, 7));
        b2.setHowLong(DayLength.FULL);
        b2.setDays(BigDecimal.valueOf(5));

        Application b4 = new Application();
        b4.setStartDate(new DateMidnight(2012, DateTimeConstants.MARCH, 29));
        b4.setEndDate(new DateMidnight(2012, DateTimeConstants.APRIL, 5));
        b4.setHowLong(DayLength.FULL);
        // must be 6 days at all: 2 before April + 4 after April

        Mockito.when(applicationDAO.getApplicationsBetweenTwoMilestones(person, firstMilestone2.toDate(),
                lastMilestone2.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(b2));

        Mockito.when(applicationDAO.getApplicationsBeforeFirstMilestone(person, firstMilestone2.toDate(),
                lastMilestone2.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(b4));

        Mockito.when(applicationDAO.getApplicationsAfterLastMilestone(person, firstMilestone2.toDate(),
                lastMilestone2.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(b1));

        Account account = new Account(person, new DateMidnight(2012, DateTimeConstants.JANUARY, 1).toDate(),
                new DateMidnight(2012, DateTimeConstants.DECEMBER, 31).toDate(), BigDecimal.valueOf(28),
                BigDecimal.valueOf(5), true);

        Mockito.when(accountService.calculateActualVacationDays(account)).thenReturn(BigDecimal.valueOf(28));
        account.setVacationDays(BigDecimal.valueOf(28));

        Mockito.when(accountService.getHolidaysAccount(2012, person)).thenReturn(account);
        Mockito.when(accountService.getOrCreateNewAccount(2012, person)).thenReturn(account);

        Application n = new Application();
        n.setStartDate(new DateMidnight(2012, DateTimeConstants.AUGUST, 20));
        n.setEndDate(new DateMidnight(2012, DateTimeConstants.AUGUST, 22));
        n.setDays(BigDecimal.valueOf(2));
        n.setPerson(person);
        n.setHowLong(DayLength.FULL);

        Assert.assertTrue(service.checkApplication(n));

        // set days to little so it can't be true
        account = new Account(person, new DateMidnight(2012, DateTimeConstants.JANUARY, 1).toDate(),
                new DateMidnight(2012, DateTimeConstants.DECEMBER, 31).toDate(), BigDecimal.valueOf(20),
                BigDecimal.valueOf(4.5), true);

        Mockito.when(accountService.calculateActualVacationDays(account)).thenReturn(BigDecimal.valueOf(20));
        account.setVacationDays(BigDecimal.valueOf(20));

        Mockito.when(accountService.getHolidaysAccount(2012, person)).thenReturn(account);
        Mockito.when(accountService.getOrCreateNewAccount(2012, person)).thenReturn(account);

        Assert.assertFalse(service.checkApplication(n));

        // set days so that sum is exact zero - so check is true
        account = new Account(person, new DateMidnight(2012, DateTimeConstants.JANUARY, 1).toDate(),
                new DateMidnight(2012, DateTimeConstants.DECEMBER, 31).toDate(), BigDecimal.valueOf(20),
                BigDecimal.valueOf(6.5), true);

        Mockito.when(accountService.calculateActualVacationDays(account)).thenReturn(BigDecimal.valueOf(20));
        account.setVacationDays(BigDecimal.valueOf(20));

        Mockito.when(accountService.getHolidaysAccount(2012, person)).thenReturn(account);
        Mockito.when(accountService.getOrCreateNewAccount(2012, person)).thenReturn(account);

        Assert.assertTrue(service.checkApplication(n));

        // remaining vacation days do not expire so it can be done
        account = new Account(person, new DateMidnight(2012, DateTimeConstants.JANUARY, 1).toDate(),
                new DateMidnight(2012, DateTimeConstants.DECEMBER, 31).toDate(), BigDecimal.valueOf(5),
                BigDecimal.valueOf(22), false);

        Mockito.when(accountService.calculateActualVacationDays(account)).thenReturn(BigDecimal.valueOf(5));
        account.setVacationDays(BigDecimal.valueOf(5));

        Mockito.when(accountService.getHolidaysAccount(2012, person)).thenReturn(account);
        Mockito.when(accountService.getOrCreateNewAccount(2012, person)).thenReturn(account);

        Assert.assertTrue(service.checkApplication(n));
    }


    @Test
    public void testCheckApplicationForNextYear() {

        Person person = new Person();
        person.setLoginName("horscht");

        DateMidnight firstMilestone = new DateMidnight(2012, DateTimeConstants.JANUARY, 1);
        DateMidnight lastMilestone = new DateMidnight(2012, DateTimeConstants.MARCH, 31);

        Application a2 = new Application();
        a2.setStartDate(new DateMidnight(2012, DateTimeConstants.MARCH, 12));
        a2.setEndDate(new DateMidnight(2012, DateTimeConstants.MARCH, 16));
        a2.setHowLong(DayLength.FULL);
        a2.setDays(BigDecimal.valueOf(5));

        Application a3 = new Application();
        a3.setStartDate(new DateMidnight(2012, DateTimeConstants.FEBRUARY, 6));
        a3.setEndDate(new DateMidnight(2012, DateTimeConstants.FEBRUARY, 9));
        a3.setHowLong(DayLength.FULL);
        a3.setDays(BigDecimal.valueOf(4));

        Application a4 = new Application();
        a4.setStartDate(new DateMidnight(2012, DateTimeConstants.MARCH, 29));
        a4.setEndDate(new DateMidnight(2012, DateTimeConstants.APRIL, 5));
        a4.setHowLong(DayLength.FULL);
        // must be 6 days at all: 2 before April + 4 after April

        Mockito.when(applicationDAO.getApplicationsBetweenTwoMilestones(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a2, a3));

        Mockito.when(applicationDAO.getApplicationsBeforeFirstMilestone(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(new ArrayList<Application>());

        Mockito.when(applicationDAO.getApplicationsAfterLastMilestone(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a4));

        DateMidnight firstMilestone2 = new DateMidnight(2012, DateTimeConstants.APRIL, 1);
        DateMidnight lastMilestone2 = new DateMidnight(2012, DateTimeConstants.DECEMBER, 31);

        Application b2 = new Application();
        b2.setStartDate(new DateMidnight(2012, DateTimeConstants.SEPTEMBER, 3));
        b2.setEndDate(new DateMidnight(2012, DateTimeConstants.SEPTEMBER, 7));
        b2.setHowLong(DayLength.FULL);
        b2.setDays(BigDecimal.valueOf(5));

        Application b4 = new Application();
        b4.setStartDate(new DateMidnight(2012, DateTimeConstants.MARCH, 29));
        b4.setEndDate(new DateMidnight(2012, DateTimeConstants.APRIL, 5));
        b4.setHowLong(DayLength.FULL);
        // must be 6 days at all: 2 before April + 4 after April

        Mockito.when(applicationDAO.getApplicationsBetweenTwoMilestones(person, firstMilestone2.toDate(),
                lastMilestone2.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(b2));

        Mockito.when(applicationDAO.getApplicationsBeforeFirstMilestone(person, firstMilestone2.toDate(),
                lastMilestone2.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(b4));

        Mockito.when(applicationDAO.getApplicationsAfterLastMilestone(person, firstMilestone2.toDate(),
                lastMilestone2.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(new ArrayList<Application>());

        Account account = new Account(person, new DateMidnight(2012, DateTimeConstants.JANUARY, 1).toDate(),
                new DateMidnight(2012, DateTimeConstants.DECEMBER, 31).toDate(), BigDecimal.valueOf(28),
                BigDecimal.valueOf(5), true);

        Mockito.when(accountService.calculateActualVacationDays(account)).thenReturn(BigDecimal.valueOf(28));
        account.setVacationDays(BigDecimal.valueOf(28));

        Application n = new Application();
        n.setStartDate(new DateMidnight(2011, DateTimeConstants.DECEMBER, 20));
        n.setEndDate(new DateMidnight(2012, DateTimeConstants.JANUARY, 3));
        n.setHowLong(DayLength.FULL);
        n.setPerson(person);
        n.setHowLong(DayLength.FULL);
        // at all there are 8 + 2 days (but only the 2 days of the new year are part of the calculation)

        Mockito.when(accountService.getHolidaysAccount(2012, person)).thenReturn(account);
        Mockito.when(accountService.getOrCreateNewAccount(2012, person)).thenReturn(account);

        Assert.assertTrue(service.checkApplication(n));

        account = new Account(person, new DateMidnight(2012, DateTimeConstants.JANUARY, 1).toDate(),
                new DateMidnight(2012, DateTimeConstants.DECEMBER, 31).toDate(), BigDecimal.valueOf(10),
                BigDecimal.valueOf(5), true);

        Mockito.when(accountService.calculateActualVacationDays(account)).thenReturn(BigDecimal.valueOf(10));
        account.setVacationDays(BigDecimal.valueOf(10));

        Mockito.when(accountService.getHolidaysAccount(2012, person)).thenReturn(account);
        Mockito.when(accountService.getOrCreateNewAccount(2012, person)).thenReturn(account);

        Assert.assertFalse(service.checkApplication(n));
    }


    @Test
    public void testGetDaysBeforeApril() {

        Person person = new Person();
        person.setLoginName("horscht");

        DateMidnight firstMilestone = new DateMidnight(2012, DateTimeConstants.JANUARY, 1);
        DateMidnight lastMilestone = new DateMidnight(2012, DateTimeConstants.MARCH, 31);

        Application a1 = new Application();
        a1.setStartDate(new DateMidnight(2011, DateTimeConstants.DECEMBER, 29));
        a1.setEndDate(new DateMidnight(2012, DateTimeConstants.JANUARY, 3));
        a1.setHowLong(DayLength.FULL);
        // must be 4 days at all: 2 before January + 2 after January

        Application a2 = new Application();
        a2.setStartDate(new DateMidnight(2012, DateTimeConstants.MARCH, 12));
        a2.setEndDate(new DateMidnight(2012, DateTimeConstants.MARCH, 16));
        a2.setHowLong(DayLength.FULL);
        a2.setDays(BigDecimal.valueOf(5));

        Application a3 = new Application();
        a3.setStartDate(new DateMidnight(2012, DateTimeConstants.FEBRUARY, 6));
        a3.setEndDate(new DateMidnight(2012, DateTimeConstants.FEBRUARY, 9));
        a3.setHowLong(DayLength.FULL);
        a3.setDays(BigDecimal.valueOf(4));

        Application a4 = new Application();
        a4.setStartDate(new DateMidnight(2012, DateTimeConstants.MARCH, 29));
        a4.setEndDate(new DateMidnight(2012, DateTimeConstants.APRIL, 5));
        a4.setHowLong(DayLength.FULL);
        // must be 6 days at all: 2 before April + 4 after April

        Mockito.when(applicationDAO.getApplicationsBetweenTwoMilestones(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a2, a3));

        Mockito.when(applicationDAO.getApplicationsBeforeFirstMilestone(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a1));

        Mockito.when(applicationDAO.getApplicationsAfterLastMilestone(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a4));

        BigDecimal days = service.getDaysBetweenTwoMilestones(person, firstMilestone, lastMilestone);
        // must be: 2 + 5 + 4 + 2 = 13

        Assert.assertNotNull(days);
        Assert.assertEquals(BigDecimal.valueOf(13).setScale(2), days);
    }


    @Test
    public void testGetDaysAfterApril() {

        Person person = new Person();
        person.setLoginName("horscht");

        DateMidnight firstMilestone = new DateMidnight(2012, DateTimeConstants.APRIL, 1);
        DateMidnight lastMilestone = new DateMidnight(2012, DateTimeConstants.DECEMBER, 31);

        Application a1 = new Application();
        a1.setStartDate(new DateMidnight(2012, DateTimeConstants.DECEMBER, 27));
        a1.setEndDate(new DateMidnight(2013, DateTimeConstants.JANUARY, 3));
        a1.setHowLong(DayLength.FULL);
        // must be 4 days at all: 2.5 before January + 2 after January

        Application a2 = new Application();
        a2.setStartDate(new DateMidnight(2012, DateTimeConstants.SEPTEMBER, 3));
        a2.setEndDate(new DateMidnight(2012, DateTimeConstants.SEPTEMBER, 7));
        a2.setHowLong(DayLength.FULL);
        a2.setDays(BigDecimal.valueOf(5));

        Application a4 = new Application();
        a4.setStartDate(new DateMidnight(2012, DateTimeConstants.MARCH, 29));
        a4.setEndDate(new DateMidnight(2012, DateTimeConstants.APRIL, 5));
        a4.setHowLong(DayLength.FULL);
        // must be 6 days at all: 2 before April + 4 after April

        Mockito.when(applicationDAO.getApplicationsBetweenTwoMilestones(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a2));

        Mockito.when(applicationDAO.getApplicationsBeforeFirstMilestone(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a4));

        Mockito.when(applicationDAO.getApplicationsAfterLastMilestone(person, firstMilestone.toDate(),
                lastMilestone.toDate(), VacationType.HOLIDAY, ApplicationStatus.WAITING, ApplicationStatus.ALLOWED))
            .thenReturn(Arrays.asList(a1));

        BigDecimal days = service.getDaysBetweenTwoMilestones(person, firstMilestone, lastMilestone);
        // must be: 2.5 + 5 + 4 = 11.5

        Assert.assertNotNull(days);
        Assert.assertEquals(BigDecimal.valueOf(11.5).setScale(2), days);
    }
}