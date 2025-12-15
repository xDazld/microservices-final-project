#!/usr/bin/env python3
"""
DNS Shield Performance Visualization Tool
Generates entertaining charts from performance test results

Author: Patrick Rafferty
"""

import sys
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.gridspec import GridSpec
import numpy as np
from datetime import datetime

# Set style for more attractive plots
plt.style.use('seaborn-v0_8-darkgrid')
plt.rcParams['figure.figsize'] = (16, 10)
plt.rcParams['font.size'] = 10


def load_data(csv_file):
    """Load performance data from CSV file"""
    df = pd.read_csv(csv_file)
    return df


def create_comprehensive_dashboard(df, output_file):
    """Create a comprehensive dashboard with multiple entertaining visualizations"""

    # Create figure with custom layout
    fig = plt.figure(figsize=(20, 12))
    gs = GridSpec(3, 3, figure=fig, hspace=0.3, wspace=0.3)

    # Color scheme
    colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A', '#98D8C8']

    # Title
    fig.suptitle('🚀 DNS Shield Performance Testing Dashboard 🚀\n' +
                 f'Generated: {datetime.now().strftime("%Y-%m-%d %H:%M:%S")}',
                 fontsize=20, fontweight='bold', y=0.98)

    # 1. Throughput vs Concurrent Users (Top Left - Large)
    ax1 = fig.add_subplot(gs[0, :2])
    for i, endpoint in enumerate(df['Endpoint'].unique()):
        endpoint_data = df[df['Endpoint'] == endpoint]
        ax1.plot(endpoint_data['Concurrent Users'], endpoint_data['Throughput (req/s)'],
                marker='o', linewidth=3, markersize=8, label=endpoint, color=colors[i % len(colors)])
    ax1.set_xlabel('Concurrent Users', fontsize=12, fontweight='bold')
    ax1.set_ylabel('Throughput (requests/second)', fontsize=12, fontweight='bold')
    ax1.set_title('📈 Throughput Scalability Under Load', fontsize=14, fontweight='bold', pad=10)
    ax1.legend(loc='best', fontsize=10)
    ax1.grid(True, alpha=0.3)
    ax1.set_facecolor('#f8f9fa')

    # 2. Latency Percentiles Comparison (Top Right)
    ax2 = fig.add_subplot(gs[0, 2])
    max_users_idx = df.groupby('Endpoint')['Concurrent Users'].idxmax()
    max_load_data = df.loc[max_users_idx]

    percentiles = ['P50 (ms)', 'P90 (ms)', 'P95 (ms)', 'P99 (ms)']
    x_pos = np.arange(len(percentiles))
    width = 0.35

    for i, endpoint in enumerate(max_load_data['Endpoint'].unique()):
        endpoint_data = max_load_data[max_load_data['Endpoint'] == endpoint]
        values = [endpoint_data[p].values[0] for p in percentiles]
        ax2.bar(x_pos + i * width, values, width, label=endpoint, color=colors[i % len(colors)], alpha=0.8)

    ax2.set_xlabel('Percentile', fontsize=11, fontweight='bold')
    ax2.set_ylabel('Latency (ms)', fontsize=11, fontweight='bold')
    ax2.set_title('⏱️ Latency at Max Load', fontsize=12, fontweight='bold', pad=10)
    ax2.set_xticks(x_pos + width / 2)
    ax2.set_xticklabels(['P50', 'P90', 'P95', 'P99'])
    ax2.legend(loc='best', fontsize=9)
    ax2.grid(True, alpha=0.3, axis='y')
    ax2.set_facecolor('#f8f9fa')

    # 3. Success Rate Gauge (Middle Left)
    ax3 = fig.add_subplot(gs[1, 0])
    avg_success_rate = df['Success Rate (%)'].mean()

    # Create a gauge-like visualization
    theta = np.linspace(0, np.pi, 100)
    r = np.ones(100)

    # Background arc
    ax3.plot(theta, r, linewidth=20, color='#e0e0e0', alpha=0.3)

    # Success rate arc
    success_theta = np.linspace(0, np.pi * (avg_success_rate / 100), 100)
    color = '#4CAF50' if avg_success_rate >= 95 else '#FFC107' if avg_success_rate >= 90 else '#F44336'
    ax3.plot(success_theta, r[:len(success_theta)], linewidth=20, color=color)

    # Add text
    ax3.text(np.pi / 2, 0.5, f'{avg_success_rate:.1f}%',
             ha='center', va='center', fontsize=32, fontweight='bold')
    ax3.text(np.pi / 2, 0.2, 'Average\nSuccess Rate',
             ha='center', va='center', fontsize=11, style='italic')

    ax3.set_ylim(0, 1.2)
    ax3.set_xlim(0, np.pi)
    ax3.axis('off')
    ax3.set_title('✅ Overall Success Rate', fontsize=12, fontweight='bold', pad=10)

    # 4. Mean vs P95 Latency Scatter (Middle Center)
    ax4 = fig.add_subplot(gs[1, 1])
    for i, endpoint in enumerate(df['Endpoint'].unique()):
        endpoint_data = df[df['Endpoint'] == endpoint]
        ax4.scatter(endpoint_data['Mean (ms)'], endpoint_data['P95 (ms)'],
                   s=endpoint_data['Concurrent Users'] * 10, alpha=0.6,
                   label=endpoint, color=colors[i % len(colors)], edgecolors='black', linewidth=1.5)

    ax4.set_xlabel('Mean Latency (ms)', fontsize=11, fontweight='bold')
    ax4.set_ylabel('P95 Latency (ms)', fontsize=11, fontweight='bold')
    ax4.set_title('🎯 Mean vs P95 Latency\n(Bubble size = concurrent users)',
                  fontsize=12, fontweight='bold', pad=10)
    ax4.legend(loc='best', fontsize=9)
    ax4.grid(True, alpha=0.3)
    ax4.set_facecolor('#f8f9fa')

    # 5. Request Distribution (Middle Right)
    ax5 = fig.add_subplot(gs[1, 2])
    total_successful = df.groupby('Endpoint')['Successful Requests'].sum()
    total_failed = df.groupby('Endpoint')['Failed Requests'].sum()

    x = np.arange(len(total_successful))
    width = 0.6

    p1 = ax5.bar(x, total_successful, width, label='Successful', color='#4CAF50', alpha=0.8)
    p2 = ax5.bar(x, total_failed, width, bottom=total_successful, label='Failed', color='#F44336', alpha=0.8)

    ax5.set_ylabel('Total Requests', fontsize=11, fontweight='bold')
    ax5.set_title('📊 Request Distribution', fontsize=12, fontweight='bold', pad=10)
    ax5.set_xticks(x)
    ax5.set_xticklabels(total_successful.index, rotation=15, ha='right')
    ax5.legend(loc='best', fontsize=10)
    ax5.grid(True, alpha=0.3, axis='y')
    ax5.set_facecolor('#f8f9fa')

    # Add value labels on bars
    for i, (succ, fail) in enumerate(zip(total_successful, total_failed)):
        if succ > 0:
            ax5.text(i, succ / 2, f'{succ:,}', ha='center', va='center', fontweight='bold', fontsize=9)
        if fail > 0:
            ax5.text(i, succ + fail / 2, f'{fail:,}', ha='center', va='center', fontweight='bold', fontsize=9)

    # 6. Latency Distribution Violin Plot (Bottom Left & Center)
    ax6 = fig.add_subplot(gs[2, :2])

    # Prepare data for violin plot
    latency_data = []
    labels = []
    positions = []
    pos = 0

    for endpoint in df['Endpoint'].unique():
        endpoint_data = df[df['Endpoint'] == endpoint]
        for _, row in endpoint_data.iterrows():
            # Simulate distribution based on percentiles
            users = row['Concurrent Users']
            # Create synthetic distribution from percentiles
            data_points = np.concatenate([
                np.random.normal(row['P50 (ms)'], row['P50 (ms)'] * 0.1, 25),
                np.random.normal(row['P90 (ms)'], row['P90 (ms)'] * 0.05, 10),
                np.random.normal(row['P95 (ms)'], row['P95 (ms)'] * 0.05, 5),
            ])
            latency_data.append(data_points)
            labels.append(f'{endpoint[:15]}\n{users} users')
            positions.append(pos)
            pos += 1

    parts = ax6.violinplot(latency_data, positions=positions, widths=0.7,
                           showmeans=True, showextrema=True)

    for i, pc in enumerate(parts['bodies']):
        endpoint_idx = i % len(df['Endpoint'].unique())
        pc.set_facecolor(colors[endpoint_idx % len(colors)])
        pc.set_alpha(0.7)

    ax6.set_xlabel('Test Configuration', fontsize=11, fontweight='bold')
    ax6.set_ylabel('Latency Distribution (ms)', fontsize=11, fontweight='bold')
    ax6.set_title('🎻 Latency Distribution Across All Tests', fontsize=12, fontweight='bold', pad=10)
    ax6.set_xticks(positions)
    ax6.set_xticklabels(labels, rotation=45, ha='right', fontsize=8)
    ax6.grid(True, alpha=0.3, axis='y')
    ax6.set_facecolor('#f8f9fa')

    # 7. Performance Score Card (Bottom Right)
    ax7 = fig.add_subplot(gs[2, 2])
    ax7.axis('off')

    # Calculate performance metrics
    max_throughput = df['Throughput (req/s)'].max()
    min_mean_latency = df['Mean (ms)'].min()
    avg_success = df['Success Rate (%)'].mean()
    total_requests = df['Successful Requests'].sum() + df['Failed Requests'].sum()

    # Performance score (0-100)
    throughput_score = min(max_throughput / 10, 40)  # Max 40 points
    latency_score = max(40 - min_mean_latency / 10, 0)  # Max 40 points
    success_score = (avg_success / 100) * 20  # Max 20 points
    performance_score = throughput_score + latency_score + success_score

    # Score card
    score_color = '#4CAF50' if performance_score >= 70 else '#FFC107' if performance_score >= 50 else '#F44336'

    scorecard_text = f"""
    ╔═══════════════════════════════╗
    ║   PERFORMANCE SCORECARD   ║
    ╚═══════════════════════════════╝

    🏆 Overall Score: {performance_score:.0f}/100

    Key Metrics:
    ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    🚀 Max Throughput:
       {max_throughput:.1f} req/s

    ⚡ Best Mean Latency:
       {min_mean_latency:.1f} ms

    ✅ Avg Success Rate:
       {avg_success:.1f}%

    📦 Total Requests:
       {total_requests:,}
    """

    ax7.text(0.1, 0.5, scorecard_text, fontsize=11, verticalalignment='center',
             fontfamily='monospace', bbox=dict(boxstyle='round', facecolor=score_color, alpha=0.2))

    # Save the figure
    plt.savefig(output_file, dpi=300, bbox_inches='tight', facecolor='white')
    print(f"✨ Comprehensive dashboard saved to: {output_file}")

    # Show the plot
    plt.show()


def main():
    if len(sys.argv) < 2:
        print("Usage: python generate_charts.py <csv_file>")
        sys.exit(1)

    csv_file = sys.argv[1]

    # Generate output filename
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    output_file = f"performance-dashboard-{timestamp}.svg"

    print("=" * 80)
    print("🎨 DNS Shield Performance Visualization Tool")
    print("=" * 80)
    print(f"📊 Loading data from: {csv_file}")

    try:
        df = load_data(csv_file)
        print(f"✓ Loaded {len(df)} test results")
        print(f"✓ Found {len(df['Endpoint'].unique())} unique endpoints")

        print("\n🎨 Generating entertaining charts...")
        create_comprehensive_dashboard(df, output_file)

        print("\n" + "=" * 80)
        print("✅ Chart generation complete!")
        print("=" * 80)

    except Exception as e:
        print(f"\n❌ Error generating charts: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)


if __name__ == "__main__":
    main()

