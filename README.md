# Overview  
Spring-rapid consists of modules encapsulating solutions for **common issues** when developing spring **REST APIs**.  
One common task that is very repetitive is implementing **crud-operations** for all of your entities.    
The core module gives you a solution for that requirering only **a few lines of code**.  
The code needed for a working controller, handling multiple different dto-types, can be seen in the example below.  
There are many other modules building upon the core module that can be plugged in when needed.  
See 'Features' below for a full list.  
  
This project is inspired by [**spring-lemon**](https://github.com/naturalprogrammer/spring-lemon) which offers many good modules as well.  
Most lemon modules were copied, modified and integrated into this project.  
  
  
# Features  
* full generic **crud** solution                                                                     (core)   
* proxy-based **extension system** for services                                                      (core)  
  -> create different kind of service beans (i.E. @Secured, @AclManaging) that use reusable extensions  
* basic exception to api-error translation                                                           (lemon-exceptions)  
* **dto-mapping** (support for different dto's for each endpoint; see example below)                 (core)  
* uni- and bidirectional relationship management for all crud operations                             (entity-relationship)  
* resolving of ids <-> entities, while mapping dto's                                                 (entity-relationship)  
* test-support for service- and controller-tests                                                     (core-test)  
* automatic **acl**-scheme setup + simple API for writing acl-based service-extensions               (acl)  
* full solution for json-web-token based **user authentication**                                     (auth)  
  -> signup, login, reset-password, verify-email, ...
    
 # Include  
 Make sure to use **java11** and **maven** with version >= **3.6.0**  
* replace MODULE with the module you want to include (module-name = directory-name)  
* replace VERSION with valid version from [releases](https://github.com/vincemann/spring-rapid/releases)  
 ### Maven  
 ```code  
<repositories>    
    <repository>   
        <id>jitpack.io</id>  
        <url>https://jitpack.io</url>  
    </repository>  
</repositories>  
  
<dependency>  
    <groupId>com.github.vincemann.spring-rapid</groupId>  
    <artifactId>MODULE</artifactId>  
    <version>VERSION</version>  
</dependency>  
```  
### Gradle  
```code
repositories {  
    jcenter()  
    maven { url "https://jitpack.io" }  
}  
dependencies {  
     implementation 'com.github.vincemann.spring-rapid:MODULE:VERSION'  
}  
```  
  
# Example  
**This is the typical setup required to run a fully functional controller exposing crud enpoints for one entity.**  
**Check out the demo module for a more complete example + tests.**  
## Controller    
  
```java  
@Controller
public class OwnerController extends CrudController<Owner, Long, OwnerService> {


    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder
                .forEndpoint(getCreateUrl(), CreateOwnerDto.class)
                .forUpdate(UpdateOwnerDto.class)
                
                
                //response dto config
                //authenticated
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forResponse(ReadOwnOwnerDto.class)
                .withPrincipal(DtoRequestInfo.Principal.FOREIGN)
                .forResponse(ReadForeignOwnerDto.class)
                //not authenticated
                .withAllPrincipals()
                .forResponse(ReadForeignOwnerDto.class)
                .build();
    }

}

```
  
  
## Service   
  
```java  
@Service  
public class JpaOwnerService  
        extends JPACrudService<Owner,Long,OwnerRepository>  
                implements OwnerService {  
}  

```  
 
  


