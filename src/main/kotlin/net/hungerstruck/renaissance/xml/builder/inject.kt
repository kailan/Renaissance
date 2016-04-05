package net.hungerstruck.renaissance.xml.builder

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class inject(val fieldName: String = "")