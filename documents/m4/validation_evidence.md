# M4 Validation Evidence (Phases 4–5 data)

## Face validation expectations vs results

- Peak rush wait (0.693h) > baseline (0.404h).
- Understaffed wait (0.545h) > baseline (0.404h).
- Ideal capacity wait (0.356h) < baseline (0.404h).
- Parts-stress Dparts (0.870h) >= baseline (0.022h).

## Extreme condition tests

- Low arrival util mean=0.0235 (expect near idle).
- Overload wait mean=5.9976h jobs=195.0 (expect long waits / backlog).
- One-tech wait mean=2.6133h util=1.3226.
- Instant parts Dparts mean=0.0000h (expect near 0).

## Parameter validation notes

- Baseline: λ=4/h, c=3, μ=2/tech/h, advisors=4, horizon=10h, Gamma k=4, α=1.
- Values align with M3 defaults calibrated for a small-shop illustration.
- Arrival profile DEALERSHIP_DAY models non-stationary daytime demand.
- Real dealership telemetry was not available; parameters are experience-based.

## Output comparison vs M/M/c (baseline replications)

- Analytical ρ mean=0.6667; simulated shop util mean=0.4202.
- Utilization relative error mean=0.3697 (std=0.1333), 95% CI [0.3220, 0.4174].
- Baseline replications with overallValid=true: 0 / 30.
- Caveat: disagreement is expected when advisors, Gamma service, and parts blocking dominate.

## Limitations

- No warm-up deletion; metrics include full 10h horizon.
- Analytical M/M/c assumes exponential service and ignores advisors/parts.
- Simulation uses Gamma service times, experience scaling, and parts blocking.
- Relative error to M/M/c is a soft benchmark, not a hard pass/fail of reality.
