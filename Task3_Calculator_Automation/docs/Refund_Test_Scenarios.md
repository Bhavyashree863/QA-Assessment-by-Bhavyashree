# Test Scenarios: Refund Validation

**Background:** Customer requests a refund 2 days after purchase.

| # | Test Condition | Expected Result | Notes |
|---|---|---|---|
| 1 | Full refund requested within refund window (2 days) | Refund approved; full amount returned to original payment method | Baseline happy path |
| 2 | Partial refund requested (e.g. refund $30 of a $100 order) | Refund approved for $30 only; order marked "partially refunded" | Order status must distinguish partial vs. full |
| 3 | Multiple partial refunds requested sequentially | Each refund succeeds until cumulative total equals original amount; further refunds blocked | Guards against double-refunding the same order |
| 4 | Refund requested to original payment method (card) | Funds returned to the same card used for purchase | Most processors mandate this; test that no alternate destination is silently allowed |
| 5 | Refund requested after the original card has expired | Refund still succeeds — issuer routes to replacement card/account, OR fails gracefully with a clear "contact your bank" message | Behavior depends on card network; test both outcomes are handled, not just assumed |
| 6 | Refund amount requested > original transaction amount | Request rejected with clear validation error | Should be blocked at the API layer, not just UI |
| 7 | Refund requested for an amount equal to $0 or negative | Request rejected with validation error | Boundary/invalid input case |
| 8 | Refund requested outside policy window (e.g. after 30 days) | Request rejected, or routed to manual review, per business policy | Confirm actual policy window with product/business, not assumed |
| 9 | Refund requested twice for the same transaction (duplicate request, e.g. double-click or retry) | Second request is idempotent — no duplicate refund issued | Test idempotency key handling / duplicate detection |
| 10 | Refund requested on a transaction that already failed/was declined originally | Request rejected — nothing to refund | Sanity check against refunding non-existent charges |
| 11 | Refund requested while the original transaction is still in "pending" (not yet settled) | Request blocked until settlement completes, or original transaction is voided instead of refunded | Void vs. refund distinction matters for unsettled transactions |
| 12 | Currency conversion: refund on a cross-currency transaction | Refunded amount matches original charged amount in original currency, accounting for FX at time of original charge | Watch for FX rate drift between charge and refund creating a mismatch |
| 13 | Refund processed but original payment method account is closed/frozen | Refund fails at the bank/issuer level; system surfaces clear failure state to support/ops, not silent success | Merchant should be notified for manual resolution |
| 14 | Concurrent refund + chargeback initiated on the same transaction | System detects conflict and prevents double-crediting the customer | Edge case but high financial risk if missed |
| 15 | Refund initiated by admin/support tool vs. by customer self-service | Both paths produce identical downstream state (order status, ledger entry, notification) | Ensures no divergent behavior between UI entry points |

**Complexity/boundary themes covered:** amount boundaries (zero,
negative, over-refund), timing boundaries (settlement state, policy
window, card expiry), idempotency/duplication, and destination
routing (original method vs. fallback).
