# Software Design and Analysis project

GaariGar was Muhammad Meeran's sixth-semester Software Design and Analysis project and placed first in his class. Its purpose was to put Java, object-oriented design, programming patterns, and code organization into practice through a complete roadside-assistance application.

## Read the boundaries in the code

1. **Layered architecture:** follow an endpoint through `CustomerController`, `CustomerService`, and `CustomerRepository`. HTTP concerns, application behavior, and persistence have separate entry points.
2. **Interface-based polymorphism:** `NotificationHandler` defines the sending contract; email and Firebase handlers implement it. `NotificationUtil` chooses applicable handlers for a user and invokes the common interface. This is strategy-style dispatch, without claiming a full event-bus or Observer implementation.
3. **Gateway abstraction:** `IPaymentGateway` separates gateway-facing operations from its Stripe implementation. The repository contains one concrete payment provider; it does not demonstrate a tested multi-provider payment system.
4. **Specification pattern:** `BaseSpecification` and entity-specific specifications keep query criteria reusable and separate from controllers.
5. **Shared domain model:** `OrderModel` represents common order state; mechanic, fuel-delivery, and standard-service order types carry their own data. DTOs and mappers distinguish API representations from persistence entities.
6. **Builder usage and dependency injection:** Lombok builders construct typed objects, while Spring injects application collaborators. These are visible implementation choices, not evidence that every SOLID principle is satisfied everywhere.

## Original work and current hosting

The Java application and Android clients originated in the course project. The public source and Railway deployment now make that work inspectable, with a framework upgrade, environment-based configuration, sample data, and access checks described in [migration status](migration-status.md). Hosting maintenance is separate from the original class result.

The current demo uses fictional records. Payments and outbound notifications are inactive, so the related classes are examples to inspect rather than a claim of live provider acceptance.
