package net.mortalsilence.indiepim.server.spring;

import org.quartz.SchedulerContext;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringBeanJobFactory extends org.springframework.scheduling.quartz.SpringBeanJobFactory {

    private String[] ignoredUnknownProperties;

    private SchedulerContext schedulerContext;

    @Override
    public void setIgnoredUnknownProperties(
            final String[] ignoredUnknownProperties) {

        super.setIgnoredUnknownProperties(ignoredUnknownProperties);
        this.ignoredUnknownProperties = ignoredUnknownProperties;
    }

    @Override
    public void setSchedulerContext(final SchedulerContext schedulerContext) {
        super.setSchedulerContext(schedulerContext);
        this.schedulerContext = schedulerContext;
    }

    /**
     * An implementation of SpringBeanJobFactory that retrieves the bean from
     * the Spring context so that autowiring and transactions work
     *
     * This method is overriden.
     * @see org.springframework.scheduling.quartz.SpringBeanJobFactory#createJobInstance(org.quartz.spi.TriggerFiredBundle)
     */
    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {

        final ConfigurableApplicationContext ctx = ((ConfigurableApplicationContext) schedulerContext.get("applicationContext"));
        final Object job = ctx.getBean(bundle.getJobDetail().getKey().getName());
        final BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(job);
        if (isEligibleForPropertyPopulation(wrapper.getWrappedInstance())) {
            final MutablePropertyValues pvs = new MutablePropertyValues();
            if (this.schedulerContext != null) {
                pvs.addPropertyValues(this.schedulerContext);
            }
            pvs.addPropertyValues(bundle.getJobDetail().getJobDataMap());
            pvs.addPropertyValues(bundle.getTrigger().getJobDataMap());
            if (this.ignoredUnknownProperties == null) {
                wrapper.setPropertyValues(pvs, true);
            } else {
                for (String propName : this.ignoredUnknownProperties) {
                    if (pvs.contains(propName)
                            && !wrapper.isWritableProperty(propName)) {

                        pvs.removePropertyValue(propName);
                    }
                }
                wrapper.setPropertyValues(pvs);
            }
        }
        return job;
    }
}