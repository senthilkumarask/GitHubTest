package com.bbb.commerce.catalog

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import javax.xml.bind.JAXBException
import spock.lang.specification.BBBExtendedSpec;

class BazaarVoiceSchedulerJobSpecification extends BBBExtendedSpec {

	BazaarVoiceSchedulerJob scJob
	BazaarVoiceUnMarshaller bzv =Mock()
	BazaarVoiceManager man =Mock()

	def setup(){
		scJob = new BazaarVoiceSchedulerJob()
		scJob.setLoggingDebug(true)
		scJob.setBazaarVoiceUnMarshaller(bzv)
		scJob.setBazaarVoiceManager(man)
		scJob.setScheduler(null)
		scJob.setSchedule(null)
		scJob.setTypeOfFeed("")
	}

	def"doScheduledTask, when scheduler is enabled"(){

		given:
		scJob.setSchedulerEnabled(true)
		1*bzv.unmarshal() >> true

		when:
		scJob.executeDoScheduledTask()

		then:
		1*man.updateScheduledRepository(_, "full", _, true)
	}

	def"doScheduledTask, when scheduler is disabled"(){

		given:
		scJob.setSchedulerEnabled(false)
		0*bzv.unmarshal()

		when:
		scJob.executeDoScheduledTask()

		then:
		0*man.updateScheduledRepository(_, "full", _, true)
	}

	def"doScheduledTask, when scheduler is enabled and BBBSystemException  is thrown"(){

		given:
		scJob.setSchedulerEnabled(true)
		1*bzv.unmarshal() >> {throw new BBBSystemException("")}

		when:
		scJob.executeDoScheduledTask()

		then:
		1*man.updateScheduledRepository(_, "full", _, false)
	}

	def"doScheduledTask, when scheduler is enabled and JAXB exeption is thrown"(){

		given:
		scJob.setSchedulerEnabled(true)
		1*bzv.unmarshal() >> {throw new JAXBException("")}

		when:
		scJob.executeDoScheduledTask()

		then:
		1*man.updateScheduledRepository(_, "full", _, false)
	}

	def"doScheduledTask, when scheduler is enabled and BBBBusinessException is thrown"(){

		given:
		scJob.setSchedulerEnabled(true)
		1*bzv.unmarshal() >> {throw new BBBBusinessException("")}

		when:
		scJob.executeDoScheduledTask()

		then:
		1*man.updateScheduledRepository(_, "full", _, false)
	}
}
