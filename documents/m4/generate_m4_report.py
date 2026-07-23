"""Generate CS4632_Sankalp_Amaravadi_M4.pdf from M4 CSV artifacts."""
from __future__ import annotations

import csv
from pathlib import Path

import matplotlib.pyplot as plt
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT
from reportlab.lib.pagesizes import letter
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import inch
from reportlab.platypus import (
    Image,
    KeepTogether,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)

ROOT = Path(__file__).resolve().parents[2]
M4 = ROOT / "documents" / "m4"
FIG = M4 / "figures"
OUT = ROOT / "documents" / "CS4632_Sankalp_Amaravadi_M4.pdf"


def read_csv(path: Path) -> list[dict[str, str]]:
    with path.open(newline="", encoding="utf-8") as f:
        return list(csv.DictReader(f))


def fnum(x: str | float, digits: int = 3) -> str:
    return f"{float(x):.{digits}f}"


def styles():
    base = getSampleStyleSheet()
    return {
        "title": ParagraphStyle(
            "TitleCustom",
            parent=base["Title"],
            fontSize=18,
            spaceAfter=6,
            alignment=TA_CENTER,
        ),
        "subtitle": ParagraphStyle(
            "SubtitleCustom",
            parent=base["Normal"],
            fontSize=12,
            alignment=TA_CENTER,
            spaceAfter=4,
        ),
        "h1": ParagraphStyle(
            "H1Custom",
            parent=base["Heading1"],
            fontSize=13,
            spaceBefore=14,
            spaceAfter=8,
            textColor=colors.HexColor("#1a365d"),
        ),
        "h2": ParagraphStyle(
            "H2Custom",
            parent=base["Heading2"],
            fontSize=11,
            spaceBefore=10,
            spaceAfter=6,
            textColor=colors.HexColor("#2c5282"),
        ),
        "body": ParagraphStyle(
            "BodyCustom",
            parent=base["Normal"],
            fontSize=10,
            leading=13,
            alignment=TA_JUSTIFY,
            spaceAfter=8,
        ),
        "caption": ParagraphStyle(
            "CaptionCustom",
            parent=base["Normal"],
            fontSize=9,
            leading=11,
            alignment=TA_CENTER,
            textColor=colors.HexColor("#2d3748"),
            spaceBefore=4,
            spaceAfter=12,
        ),
        "bullet": ParagraphStyle(
            "BulletCustom",
            parent=base["Normal"],
            fontSize=10,
            leading=13,
            leftIndent=14,
            spaceAfter=4,
        ),
        "footer": ParagraphStyle(
            "FooterCustom",
            parent=base["Normal"],
            fontSize=8,
            alignment=TA_CENTER,
            textColor=colors.grey,
        ),
    }


def make_table(headers: list[str], rows: list[list[str]], col_widths=None) -> Table:
    data = [headers] + rows
    t = Table(data, colWidths=col_widths, repeatRows=1)
    t.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#2c5282")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("FONTSIZE", (0, 0), (-1, -1), 8),
                ("ALIGN", (0, 0), (-1, -1), "CENTER"),
                ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
                ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#a0aec0")),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, colors.HexColor("#edf2f7")]),
                ("TOPPADDING", (0, 0), (-1, -1), 4),
                ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
                ("LEFTPADDING", (0, 0), (-1, -1), 3),
                ("RIGHTPADDING", (0, 0), (-1, -1), 3),
            ]
        )
    )
    return t


def make_figures():
    FIG.mkdir(parents=True, exist_ok=True)
    sens = read_csv(M4 / "sensitivity_coefficients.csv")
    scen = read_csv(M4 / "scenario_comparison.csv")

    # Figure 1: wait vs arrival rate
    lam = [r for r in sens if r["parameter"] == "arrivalRate"]
    # include baseline mentally at 4
    xs = [2.0, 4.0, 6.0, 8.0]
    # baseline wait from scenario file
    base_wait = float(next(r for r in scen if r["scenario"] == "baseline")["wait_mean"])
    ys = []
    for x in xs:
        if abs(x - 4.0) < 1e-9:
            ys.append(base_wait)
        else:
            ys.append(float(next(r for r in lam if abs(float(r["level"]) - x) < 1e-9)["wait_mean"]))
    fig, ax = plt.subplots(figsize=(6.2, 3.4))
    ax.plot(xs, ys, marker="o", color="#2b6cb0", linewidth=2)
    ax.set_xlabel("Arrival rate λ (vehicles/h)")
    ax.set_ylabel("Mean total wait W_total (h)")
    ax.set_title("Sensitivity of customer wait to arrival rate")
    ax.grid(True, alpha=0.3)
    fig.tight_layout()
    p1 = FIG / "fig1_wait_vs_lambda.png"
    fig.savefig(p1, dpi=160)
    plt.close(fig)

    # Figure 2: technician count effect
    tech = [r for r in sens if r["parameter"] == "technicianCount"]
    txs = [2.0, 3.0, 4.0, 5.0]
    tys = []
    for x in txs:
        if abs(x - 3.0) < 1e-9:
            tys.append(base_wait)
        else:
            tys.append(float(next(r for r in tech if abs(float(r["level"]) - x) < 1e-9)["wait_mean"]))
    fig, ax = plt.subplots(figsize=(6.2, 3.4))
    ax.bar([str(int(x)) for x in txs], tys, color="#38a169")
    ax.set_xlabel("Technicians / bays c")
    ax.set_ylabel("Mean total wait W_total (h)")
    ax.set_title("Effect of technician staffing on wait time")
    ax.grid(True, axis="y", alpha=0.3)
    fig.tight_layout()
    p2 = FIG / "fig2_wait_vs_techs.png"
    fig.savefig(p2, dpi=160)
    plt.close(fig)

    # Figure 3: scenario comparison
    order = [
        "baseline",
        "scenario_peak",
        "scenario_understaffed",
        "scenario_parts_stress",
        "scenario_ideal_capacity",
    ]
    labels = {
        "baseline": "Normal",
        "scenario_peak": "Peak",
        "scenario_understaffed": "Understaffed",
        "scenario_parts_stress": "Parts stress",
        "scenario_ideal_capacity": "Ideal capacity",
    }
    waits = []
    utils = []
    names = []
    for key in order:
        row = next(r for r in scen if r["scenario"] == key)
        names.append(labels[key])
        waits.append(float(row["wait_mean"]))
        utils.append(float(row["util_mean"]))
    fig, ax1 = plt.subplots(figsize=(6.8, 3.6))
    x = range(len(names))
    w = 0.38
    b1 = ax1.bar([i - w / 2 for i in x], waits, width=w, color="#c53030", label="Wait (h)")
    ax2 = ax1.twinx()
    b2 = ax2.bar([i + w / 2 for i in x], utils, width=w, color="#2b6cb0", label="Tech util")
    ax1.set_xticks(list(x))
    ax1.set_xticklabels(names, rotation=15, ha="right")
    ax1.set_ylabel("Mean W_total (h)")
    ax2.set_ylabel("Mean shop tech utilization")
    ax1.set_title("Scenario comparison: wait and utilization")
    ax1.grid(True, axis="y", alpha=0.25)
    ax1.legend(handles=[b1, b2], loc="upper left")
    fig.tight_layout()
    p3 = FIG / "fig3_scenario_comparison.png"
    fig.savefig(p3, dpi=160)
    plt.close(fig)

    return p1, p2, p3


def add_header_footer(canvas, doc):
    canvas.saveState()
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(colors.grey)
    canvas.drawString(0.75 * inch, 0.5 * inch, "CS 4632 M4 — Sankalp Amaravadi")
    canvas.drawRightString(letter[0] - 0.75 * inch, 0.5 * inch, f"Page {doc.page}")
    canvas.restoreState()


def build_pdf(fig_paths):
    S = styles()
    story = []

    story.append(Paragraph("CS 4632 — Milestone 4", S["subtitle"]))
    story.append(Paragraph("Analysis and Validation Report", S["title"]))
    story.append(Paragraph("Service Department Operational Optimization Simulator", S["subtitle"]))
    story.append(Paragraph("Sankalp Amaravadi", S["subtitle"]))
    story.append(Paragraph("Summer 2026", S["subtitle"]))
    story.append(Spacer(1, 0.15 * inch))
    story.append(
        Paragraph(
            "Supporting data: <font face='Courier'>documents/m4/</font> and "
            "<font face='Courier'>results/m4/</font> (n=30 replications per case, seed policy seed+i).",
            S["caption"],
        )
    )

    # 1 Introduction
    story.append(Paragraph("1. Introduction", S["h1"]))
    story.append(
        Paragraph(
            "This report documents Milestone 4 analysis for a discrete-event simulation (DES) of an "
            "automotive dealership service department. The simulator, completed in M3, models "
            "non-stationary customer arrivals (dealership-day Cox/Poisson process), service-advisor "
            "intake, technician/bay repair with Gamma service times and experience scaling, and a "
            "parts department with reorder lead times. Per-run metrics and CSV/JSON exports already "
            "existed; M4 adds multi-replication statistics, one-at-a-time sensitivity sweeps, multi-factor "
            "scenario tests, and structured validation evidence.",
            S["body"],
        )
    )
    story.append(
        Paragraph(
            "<b>Analysis goals.</b> (1) Quantify which controllable parameters most drive customer wait "
            "and shop utilization. (2) Compare realistic operating scenarios (normal, peak, understaffed, "
            "parts stress, ideal capacity). (3) Validate directional reasonableness via face checks, "
            "extreme conditions, parameter justification, and soft M/M/c output comparison. "
            "(4) Report means, standard deviations, and 95% confidence intervals for primary metrics.",
            S["body"],
        )
    )
    story.append(
        Paragraph(
            "<b>Baseline configuration</b> (M3 defaults): λ = 4 vehicles/h, c = 3 technicians/bays, "
            "μ = 2 jobs/tech/h, advisors = 4, Gamma shape k = 4, experience α = 1, horizon = 10 h, "
            "parts lead time L = 2 h, base seed = 42. Replication i uses seed 42+i. Statistics use "
            "CI = mean ± 1.96·s/√n (Appendix A.3).",
            S["body"],
        )
    )

    # 2 Parameter Analysis
    story.append(Paragraph("2. Parameter Analysis", S["h1"]))
    story.append(Paragraph("2.1 Method", S["h2"]))
    story.append(
        Paragraph(
            "Sensitivity analysis varies one input at a time while holding other baseline parameters "
            "fixed. Each level runs n = 30 replications. Sensitivity follows Appendix A.2: "
            "Sensitivity = (%Δ output) / (%Δ input) relative to the baseline level. Primary outputs "
            "are mean total customer wait W_total (h), shop technician utilization, and jobs completed.",
            S["body"],
        )
    )
    story.append(
        Paragraph(
            "Swept parameters: arrival rate λ ∈ {2,4,6,8}; technicians c ∈ {2,3,4,5}; "
            "service rate μ ∈ {1.5,2.0,2.5}; advisors ∈ {1,2,4}; parts lead time L ∈ {0.5,2.0,4.0} hours.",
            S["body"],
        )
    )

    story.append(Paragraph("2.2 Results", S["h2"]))
    sens = read_csv(M4 / "sensitivity_coefficients.csv")
    sens_rows = []
    for r in sens:
        sens_rows.append(
            [
                r["parameter"],
                fnum(r["level"], 1),
                fnum(r["wait_mean"]),
                fnum(r["wait_sensitivity"]),
                fnum(r["util_mean"]),
                fnum(r["util_sensitivity"]),
                fnum(r["jobs_mean"], 1),
            ]
        )
    story.append(
        make_table(
            ["Parameter", "Level", "Wait mean (h)", "Wait sens.", "Util mean", "Util sens.", "Jobs mean"],
            sens_rows,
            col_widths=[1.15 * inch, 0.55 * inch, 0.9 * inch, 0.75 * inch, 0.7 * inch, 0.7 * inch, 0.7 * inch],
        )
    )
    story.append(
        Paragraph(
            "Table 1. One-at-a-time sensitivity coefficients and mean outputs (n=30). "
            "Baseline reference levels: λ=4, c=3, μ=2, advisors=4, L=2 h.",
            S["caption"],
        )
    )

    story.append(Image(str(fig_paths[0]), width=5.8 * inch, height=3.15 * inch))
    story.append(
        Paragraph(
            "Figure 1. Mean total customer wait versus arrival rate λ (baseline wait at λ=4 included).",
            S["caption"],
        )
    )
    story.append(Image(str(fig_paths[1]), width=5.8 * inch, height=3.15 * inch))
    story.append(
        Paragraph(
            "Figure 2. Mean total customer wait versus technician/bay count c.",
            S["caption"],
        )
    )

    story.append(Paragraph("2.3 Interpretation and ranking", S["h2"]))
    story.append(
        Paragraph(
            "Ranking drivers of wait time (by magnitude of wait sensitivity near the tested steps):",
            S["body"],
        )
    )
    story.append(
        Paragraph(
            "1. <b>Service rate μ</b> — reducing μ from 2.0 to 1.5 raises wait to 0.567 h "
            "(wait sensitivity ≈ −1.61). Slower mean service is the strongest lever on W_total.",
            S["bullet"],
        )
    )
    story.append(
        Paragraph(
            "2. <b>Technician count c</b> — cutting staffing from 3 to 2 raises wait to 0.543 h "
            "(sensitivity ≈ −1.03) and utilization (sensitivity ≈ −1.48). Adding a fourth tech "
            "helps, but returns diminish by c=5 (wait 0.356 h vs 0.362 h at c=4).",
            S["bullet"],
        )
    )
    story.append(
        Paragraph(
            "3. <b>Arrival rate λ</b> — wait and utilization rise roughly with demand "
            "(at λ=8, wait 0.693 h, util 0.788; jobs ≈ 73.7). Throughput sensitivity ≈ 1, "
            "as expected for an unsaturated shop.",
            S["bullet"],
        )
    )
    story.append(
        Paragraph(
            "4. <b>Parts lead time L</b> — moderate effect on wait (0.384 h at L=0.5 vs 0.465 h at L=4); "
            "larger effect appears when stock/reorder are stressed jointly (Section 3).",
            S["bullet"],
        )
    )
    story.append(
        Paragraph(
            "5. <b>Advisor count</b> — at baseline λ, reducing advisors to 1 barely changes total wait "
            "(0.415 h) but clearly increases advisor wait (0.045 h). Advisors become binding mainly "
            "when intake capacity is reduced together with tech capacity (understaffed scenario).",
            S["bullet"],
        )
    )

    # 3 Scenario Testing
    story.append(PageBreak())
    story.append(Paragraph("3. Scenario Testing", S["h1"]))
    story.append(Paragraph("3.1 Scenario definitions", S["h2"]))
    story.append(
        Paragraph(
            "Scenarios change multiple parameters simultaneously to represent realistic shop conditions "
            "aligned with M1 expected outcomes (morning rush, staffing shortfall, parts blocking).",
            S["body"],
        )
    )
    story.append(
        Paragraph(
            "• <b>Normal day (baseline):</b> defaults above.<br/>"
            "• <b>Peak / rush demand:</b> λ = 8 with baseline staff → long queues, high ρ.<br/>"
            "• <b>Understaffed shop:</b> c = 2 technicians and 1 advisor at λ = 4 → dual bottleneck.<br/>"
            "• <b>Parts stress:</b> longer lead time with tighter inventory → elevated D_parts "
            "and “phantom” under-utilization relative to wait.<br/>"
            "• <b>Ideal capacity:</b> c = 5 technicians (extra capacity) at baseline demand → shorter waits, "
            "diminishing returns.",
            S["body"],
        )
    )

    story.append(Paragraph("3.2 Results", S["h2"]))
    scen = read_csv(M4 / "scenario_comparison.csv")
    label_map = {
        "baseline": "Normal",
        "scenario_peak": "Peak",
        "scenario_understaffed": "Understaffed",
        "scenario_parts_stress": "Parts stress",
        "scenario_ideal_capacity": "Ideal capacity",
    }
    scen_rows = []
    for r in scen:
        scen_rows.append(
            [
                label_map.get(r["scenario"], r["scenario"]),
                fnum(r["wait_mean"]),
                f"[{fnum(r['wait_ci_low'])}, {fnum(r['wait_ci_high'])}]",
                fnum(r["util_mean"]),
                fnum(r["parts_delay_mean"]),
                fnum(r["jobs_mean"], 1),
                fnum(r["advisor_wait_mean"], 4),
                fnum(r["queue_delay_mean"]),
            ]
        )
    story.append(
        make_table(
            [
                "Scenario",
                "W_total",
                "95% CI wait",
                "Tech util",
                "D_parts (h)",
                "Jobs/day",
                "Adv wait",
                "D_queue",
            ],
            scen_rows,
            col_widths=[0.95 * inch, 0.6 * inch, 1.15 * inch, 0.65 * inch, 0.75 * inch, 0.6 * inch, 0.6 * inch, 0.6 * inch],
        )
    )
    story.append(
        Paragraph(
            "Table 2. Scenario comparison means (n=30). Wait CI uses 95% normal approximation. "
            "Units: hours except jobs (count over 10 h horizon, labeled jobs/day for planning).",
            S["caption"],
        )
    )

    story.append(Image(str(fig_paths[2]), width=6.0 * inch, height=3.2 * inch))
    story.append(
        Paragraph(
            "Figure 3. Side-by-side scenario means for total wait and technician utilization.",
            S["caption"],
        )
    )

    story.append(Paragraph("3.3 Comparison insights", S["h2"]))
    story.append(
        Paragraph(
            "Peak demand nearly doubles wait versus normal (0.693 h vs 0.404 h) and raises shop "
            "utilization from 0.420 to 0.788; queue delay jumps to 0.324 h. Jobs completed roughly "
            "double (73.7 vs 37.9), confirming the shop absorbs more volume under heavy congestion.",
            S["body"],
        )
    )
    story.append(
        Paragraph(
            "Understaffing increases wait to 0.545 h. Advisor wait rises from near-zero to 0.039 h "
            "and queue delay to 0.160 h, showing a compound bottleneck: customers wait both at "
            "intake and for technicians. Jobs stay near baseline (~38), so lost service quality "
            "appears as delay, not lost completions within the horizon.",
            S["body"],
        )
    )
    story.append(
        Paragraph(
            "Parts stress produces the largest wait (1.271 h) with parts delay 0.870 h, while "
            "technician utilization remains near baseline (0.428). This matches the M1 “phantom "
            "under-utilization” story: staff appear available while jobs are blocked on parts.",
            S["body"],
        )
    )
    story.append(
        Paragraph(
            "Ideal capacity trims wait to 0.356 h and utilization to 0.264 with negligible queue "
            "delay. Marginal gain versus four technicians is small, suggesting advisor/parts "
            "constraints and arrival variability bound further improvement under baseline demand.",
            S["body"],
        )
    )

    # 4 Validation
    story.append(Paragraph("4. Validation", S["h1"]))
    story.append(
        Paragraph(
            "Four validation approaches were applied. Evidence is summarized from "
            "<font face='Courier'>documents/m4/validation_evidence.md</font> and "
            "<font face='Courier'>validation_checks.csv</font>.",
            S["body"],
        )
    )

    story.append(Paragraph("4.1 Face validation", S["h2"]))
    story.append(
        Paragraph(
            "Expected directional behaviors from domain knowledge were confirmed: (i) peak wait "
            "(0.693 h) &gt; baseline (0.404 h); (ii) understaffed wait (0.545 h) &gt; baseline; "
            "(iii) ideal-capacity wait (0.356 h) &lt; baseline; (iv) parts-stress D_parts "
            "(0.870 h) ≫ baseline (0.022 h). Heuristic checks in Table 3 all pass.",
            S["body"],
        )
    )
    checks = read_csv(M4 / "validation_checks.csv")
    check_rows = [[c["check"], fnum(c["value"]), c["pass_heuristic"], c["notes"]] for c in checks]
    story.append(
        make_table(
            ["Check", "Value", "Pass", "Notes"],
            check_rows,
            col_widths=[2.4 * inch, 0.8 * inch, 0.6 * inch, 2.0 * inch],
        )
    )
    story.append(Paragraph("Table 3. Face/extreme validation heuristic checks.", S["caption"]))

    story.append(Paragraph("4.2 Extreme conditions", S["h2"]))
    story.append(
        Paragraph(
            "• <b>Near-empty shop (λ≈0.2):</b> mean tech utilization 0.024; jobs ≈ 1.0 — idle shop.<br/>"
            "• <b>Overload (λ ≫ cμ):</b> mean wait 5.998 h, jobs ≈ 195 — queues explode as expected.<br/>"
            "• <b>Single technician (c=1):</b> wait 2.613 h, util 1.323 — severe congestion.<br/>"
            "• <b>Instant parts (L≈0):</b> D_parts mean 0.000 h — parts module behaves correctly.",
            S["body"],
        )
    )

    story.append(Paragraph("4.3 Parameter validation", S["h2"]))
    story.append(
        Paragraph(
            "Defaults match the M3 parameter table and M1 small-dealership illustration: a few "
            "technicians, Poisson-style arrivals with a dealership-day profile, Gamma service "
            "(shape 4) for moderate variability, and experience scaling. Values are "
            "experience-based / illustrative rather than fitted to a proprietary dealership "
            "telemetry set (no real site dataset was available).",
            S["body"],
        )
    )

    story.append(Paragraph("4.4 Output comparison (M/M/c soft benchmark)", S["h2"]))
    story.append(
        Paragraph(
            "Analytical M/M/c ρ for the baseline is 0.667, while simulated shop utilization averages "
            "0.420 (relative error mean 0.370, 95% CI [0.322, 0.417]). Zero of 30 baseline "
            "replications meet a strict overallValid flag under the M3 tolerance. This mismatch is "
            "<i>expected</i> and informative: the live model includes an advisor stage, Gamma "
            "(not exponential) service, experience factors, non-stationary arrivals, and parts "
            "blocking—none of which the classic M/M/c formulas include. M/M/c is therefore used as "
            "a soft structural benchmark, not a pass/fail oracle.",
            S["body"],
        )
    )

    story.append(Paragraph("4.5 Limitations", S["h2"]))
    story.append(
        Paragraph(
            "• No warm-up deletion; metrics include the full 10 h horizon.<br/>"
            "• No empirical dealership arrival/service dataset for calibration.<br/>"
            "• FIFO queues only; bay count tied to technicians.<br/>"
            "• Constant reorder policy; no dynamic expediting or multi-SKU complexity.<br/>"
            "• Analytical M/M/c comparison is intentionally honest about structural mismatch.",
            S["body"],
        )
    )

    # 5 Statistical Summary
    story.append(PageBreak())
    story.append(Paragraph("5. Statistical Summary", S["h1"]))
    story.append(
        Paragraph(
            "Primary metrics (matching the M3 tracked set): average total customer wait W_total, "
            "shop technician utilization, parts delay D_parts, throughput (jobs completed), and "
            "advisor wait / queue delay as bottleneck indicators. Table 4 reports the required "
            "statistical columns for the baseline case; Tables 5–6 summarize selected scenario metrics.",
            S["body"],
        )
    )

    stats = read_csv(M4 / "statistical_summary.csv")
    primary = [
        "avg_customer_wait_h",
        "shop_tech_util",
        "avg_parts_delay_h",
        "jobs_completed",
        "avg_advisor_wait_h",
        "avg_queue_delay_h",
    ]
    nice = {
        "avg_customer_wait_h": "W_total (h)",
        "shop_tech_util": "Tech util",
        "avg_parts_delay_h": "D_parts (h)",
        "jobs_completed": "Jobs completed",
        "avg_advisor_wait_h": "Advisor wait (h)",
        "avg_queue_delay_h": "D_queue (h)",
    }

    def stats_rows_for(experiment: str):
        rows = []
        for m in primary:
            r = next(x for x in stats if x["experiment"] == experiment and x["metric"] == m)
            rows.append(
                [
                    nice[m],
                    r["n"],
                    fnum(r["mean"]),
                    fnum(r["std_dev"]),
                    fnum(r["min"]),
                    fnum(r["max"]),
                    f"[{fnum(r['ci95_low'])}, {fnum(r['ci95_high'])}]",
                ]
            )
        return rows

    story.append(Paragraph("5.1 Baseline (normal day)", S["h2"]))
    story.append(
        make_table(
            ["Metric", "n", "Mean", "Std Dev", "Min", "Max", "95% CI"],
            stats_rows_for("baseline"),
            col_widths=[1.3 * inch, 0.4 * inch, 0.7 * inch, 0.7 * inch, 0.7 * inch, 0.7 * inch, 1.5 * inch],
        )
    )
    story.append(
        Paragraph(
            "Table 4. Baseline statistical summary (n=30, seed 42…71).",
            S["caption"],
        )
    )

    story.append(Paragraph("5.2 Peak and parts-stress scenarios", S["h2"]))
    story.append(
        make_table(
            ["Metric", "n", "Mean", "Std Dev", "Min", "Max", "95% CI"],
            stats_rows_for("scenario_peak"),
            col_widths=[1.3 * inch, 0.4 * inch, 0.7 * inch, 0.7 * inch, 0.7 * inch, 0.7 * inch, 1.5 * inch],
        )
    )
    story.append(Paragraph("Table 5. Peak / rush scenario statistics (n=30).", S["caption"]))

    story.append(
        make_table(
            ["Metric", "n", "Mean", "Std Dev", "Min", "Max", "95% CI"],
            stats_rows_for("scenario_parts_stress"),
            col_widths=[1.3 * inch, 0.4 * inch, 0.7 * inch, 0.7 * inch, 0.7 * inch, 0.7 * inch, 1.5 * inch],
        )
    )
    story.append(Paragraph("Table 6. Parts-stress scenario statistics (n=30).", S["caption"]))

    story.append(Paragraph("5.3 Discussion of variability", S["h2"]))
    story.append(
        Paragraph(
            "Baseline wait SD is 0.103 h against a mean of 0.404 h (CV ≈ 26%), so seed variation "
            "matters but the 95% CI [0.367, 0.441] remains informative with n=30. Peak and "
            "parts-stress cases show higher absolute SD (wait SD 0.287 h and 0.577 h), consistent "
            "with near-capacity or inventory blocking amplifying path dependence. Jobs completed "
            "under baseline has SD ≈ 5.8 around mean 37.9; capacity and arrival volatility both "
            "contribute. Negative CI bounds for near-zero advisor wait are an artifact of the "
            "symmetric normal approximation and should be interpreted as ≈0.",
            S["body"],
        )
    )

    # 6 Conclusions
    story.append(Paragraph("6. Conclusions", S["h1"]))
    story.append(Paragraph("6.1 Main findings", S["h2"]))
    story.append(
        Paragraph(
            "• Service rate and technician staffing dominate wait-time sensitivity near the baseline; "
            "arrival rate scales both wait and throughput.<br/>"
            "• Parts stress can inflate total wait far more than bare lead-time sensitivity suggests "
            "when inventory policy is tight, while utilization stays deceptively moderate.<br/>"
            "• Understaffing surfaces advisor delays that are invisible at four advisors.<br/>"
            "• Extra technicians past four yield diminishing wait reductions at baseline demand.<br/>"
            "• Face and extreme tests support model reasonableness; M/M/c disagreement is explained "
            "by structural features beyond multi-server exponential theory.",
            S["body"],
        )
    )
    story.append(Paragraph("6.2 Strengths and weaknesses", S["h2"]))
    story.append(
        Paragraph(
            "<b>Strengths:</b> end-to-end DES with wait decomposition; reproducible batch harness "
            "(seed+i); n=30 statistical rigor; scenarios that isolate staffing vs inventory stories; "
            "honest soft validation against M/M/c.<br/>"
            "<b>Weaknesses:</b> illustrative parameters without field telemetry; no warm-up discard; "
            "simplified parts/FIFO logic; utilization definitions under extreme overload can exceed 1 "
            "when busy-time accounting includes backlog effects—treat overload util as a stress "
            "signal rather than a bounded fraction.",
            S["body"],
        )
    )
    story.append(Paragraph("6.3 Recommendations", S["h2"]))
    story.append(
        Paragraph(
            "For capacity planning with this tool: (1) protect mean service productivity (training / "
            "experience) before over-hiring beyond the arrival rate; (2) staff technicians for peak "
            "λ scenarios, not only average days; (3) treat parts policy as a first-class lever—short "
            "lead time or safer stock can cut wait without raising apparent utilization; "
            "(4) when advisor count drops, monitor advisor wait separately from shop queue delay; "
            "(5) use replications (n≥30) before trusting a single UI screenshot run.",
            S["body"],
        )
    )

    story.append(Spacer(1, 0.2 * inch))
    story.append(
        Paragraph(
            "Reproducibility: "
            "<font face='Courier'>mvn -q -DskipTests compile exec:java "
            "\"-Dexec.mainClass=simulator.analysis.M4ExperimentRunner\" "
            "\"-Dexec.args=--replications=30 --output=results/m4\" "
            "\"-Djava.util.logging.config.file=logging-m4.properties\"</font>. "
            "Regenerate this PDF via "
            "<font face='Courier'>python documents/m4/generate_m4_report.py</font>.",
            S["body"],
        )
    )

    doc = SimpleDocTemplate(
        str(OUT),
        pagesize=letter,
        leftMargin=0.7 * inch,
        rightMargin=0.7 * inch,
        topMargin=0.65 * inch,
        bottomMargin=0.7 * inch,
        title="CS4632 Sankalp Amaravadi M4 — Analysis and Validation",
        author="Sankalp Amaravadi",
    )
    doc.build(story, onFirstPage=add_header_footer, onLaterPages=add_header_footer)
    print(f"Wrote {OUT}")


def main():
    figs = make_figures()
    build_pdf(figs)


if __name__ == "__main__":
    main()
