Technology stack: Java Spring Boot 3 with Java 23 Dependencies (ALPHA)

Application Logic Design：

1. All request and response handling must be done only in RestController.
2. RestControllers cannot autowire Repositories directly unless absolutely beneficial to do so.
3. ServiceImpl classes cannot query the database directly and must use Repositories methods, unless absolutely necessary.
4. Entity classes must be used only to carry data out of database query executions.

Service：

1. Service classes must be of type interface.
2. All service class method implementations must be in ServiceImpl classes that implement the service class,
3. All ServiceImpl classes must be annotated with @Service.
4. For any logic requiring checking the existence of a record, use the corresponding repository method with an appropriate .orElseThrow lambda method.
5. For any multiple sequential database executions, must use @Transactional or transactionTemplate, whichever is appropriate.

RestController:

1. Must annotate controller classes with @RestController.
2. Must specify class-level API routes with @RequestMapping, e.g. ("/api/user").
3. Class methods must use best practice HTTP method annotations, e.g, create = @postMapping("/create"), etc.
4. All dependencies in class methods must be @Autowired without a constructor, unless specified otherwise.
5. All class method logic must be implemented in a try..catch block(s).
6. Add always a GlobalExceptionHandler class based on ControllerAdvice to handle all exceptions.
