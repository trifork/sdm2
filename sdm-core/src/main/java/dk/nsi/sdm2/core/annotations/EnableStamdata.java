package dk.nsi.sdm2.core.annotations;

@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@org.springframework.context.annotation.Import({dk.nsi.sdm2.core.config.StamdataConfiguration.class})
@java.lang.annotation.Documented
public @interface EnableStamdata {
    String home();
}
