package dk.nsi.sdm4.core.annotations;

@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@org.springframework.context.annotation.Import({dk.nsi.sdm4.core.config.StamdataConfiguration.class})
public @interface EnableStamdata {
    String home();
}
