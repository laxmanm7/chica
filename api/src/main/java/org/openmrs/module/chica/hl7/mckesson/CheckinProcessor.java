/**
 * 
 */
package org.openmrs.module.chica.hl7.mckesson;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hl7.PrinterLocationHL7Filter;
import org.openmrs.module.chica.hl7.CurrentDateHL7Filter;
import org.openmrs.module.sockethl7listener.HL7Filter;
import org.openmrs.module.sockethl7listener.HL7ObsHandler25;
import org.openmrs.module.sockethl7listener.SimpleServer;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

import ca.uhn.hl7v2.llp.LowerLayerProtocol;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * @author Tammy Dugan
 * 
 */
public class CheckinProcessor extends AbstractTask
{
	private Log log = LogFactory.getLog(this.getClass());
	private SimpleServer server = null;
	
	@Override
	public void initialize(TaskDefinition config)
	{
		this.log.info("Initializing McKesson checkin processor...");
		super.initialize(config);
		AdministrationService adminService = Context.getAdministrationService();

		String portString = this.taskDefinition.getProperty("port");

		if (portString == null)
		{
			portString = adminService
					.getGlobalProperty("sockethl7listener.port");
		}

		try
		{
			Integer port = null;
			
			try
			{
				port = Integer.parseInt(portString);
			} catch (NumberFormatException e)
			{
				this.log.error("Could not start SimpleServer. Port "+portString+" could not be parsed");
				return;
			}
			PipeParser parser = new PipeParser();
			parser.setValidationContext(new NoValidation());
			PatientHandler patientHandler = new PatientHandler();
			PrinterLocationHL7Filter printerLocationHL7Filter = new PrinterLocationHL7Filter();
			CurrentDateHL7Filter currentDateHL7Filter = new CurrentDateHL7Filter();
			ArrayList<HL7Filter> filters = new ArrayList<HL7Filter>();
			filters.add(printerLocationHL7Filter);
			filters.add(currentDateHL7Filter);
			HL7SocketHandler socketHandler = new HL7SocketHandler(parser,
					patientHandler, new HL7ObsHandler25(), new HL7EncounterHandler25(),
					new HL7PatientHandler25(),filters);
			socketHandler.setPort(Integer.valueOf(portString));
			this.server = new SimpleServer(port, LowerLayerProtocol
					.makeLLP(),parser,patientHandler, socketHandler);
			log.info("Starting SimpleServer...");
		} catch (Exception e)
		{
			log.error("Error starting SimpleServer...");
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		this.log.info("Finished initializing McKesson checkin processor.");
	}

	@Override
	public void execute()
	{
		Context.openSession();
		try
		{
			this.server.start();
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		} finally
		{
			Context.closeSession();
		}
	}

	@Override
	public void shutdown()
	{
		super.shutdown();
		try
		{
			if(this.server != null){
				// CHICA-221 Use stopAndWait() instead of stop()
				// Openmrs added multiple calls to SchedulerUtil.startUp() which could potentially
				// allow multiple instances of this task to run at the same time or causes errors when starting
				// the second instance which would cause zero instances to be running since the first one will eventually shutdown
				// CHICA-961 Add shutdownNow() to allow the application to shutdown faster
				server.getExecutorService().shutdownNow();
				this.server.stopAndWait();
				
				
				// Adding this delay due to limitations with Hapi. Hapi does not call AcceptorThread.stopAndWait().
				// AcceptorThread.stop() is used which does not guarantee that the socket has had time to close
				Thread.sleep(3000);
			}
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}	
	}
}
