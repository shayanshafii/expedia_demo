#!/usr/bin/env python3
"""
Parse JaCoCo XML report and generate a markdown coverage table.
"""
import xml.etree.ElementTree as ET
import sys
import os

def parse_jacoco_xml(xml_path):
    """Parse JaCoCo XML and extract coverage metrics."""
    if not os.path.exists(xml_path):
        print(f"Error: JaCoCo XML file not found at {xml_path}", file=sys.stderr)
        sys.exit(1)
    
    tree = ET.parse(xml_path)
    root = tree.getroot()
    
    # Extract overall coverage from report level counters
    counters = root.findall('.//counter')
    metrics = {}
    
    for counter in counters:
        counter_type = counter.get('type')
        missed = int(counter.get('missed', 0))
        covered = int(counter.get('covered', 0))
        total = missed + covered
        
        if total > 0:
            coverage = (covered / total) * 100
            metrics[counter_type] = {
                'missed': missed,
                'covered': covered,
                'total': total,
                'coverage': coverage
            }
    
    # Extract package-level coverage
    packages = []
    for package in root.findall('.//package'):
        pkg_name = package.get('name', 'default')
        pkg_counters = package.findall('counter')
        
        pkg_metrics = {}
        for counter in pkg_counters:
            counter_type = counter.get('type')
            if counter_type == 'INSTRUCTION':
                missed = int(counter.get('missed', 0))
                covered = int(counter.get('covered', 0))
                total = missed + covered
                if total > 0:
                    coverage = (covered / total) * 100
                    pkg_metrics['coverage'] = coverage
                    pkg_metrics['total'] = total
        
        if 'coverage' in pkg_metrics:
            # Clean up package name for display
            display_name = pkg_name.replace('com.expedia.demo.', '')
            if display_name:
                packages.append({
                    'name': display_name,
                    'coverage': pkg_metrics['coverage'],
                    'total': pkg_metrics['total']
                })
    
    # Sort packages by coverage (lowest first)
    packages.sort(key=lambda x: x['coverage'])
    
    return metrics, packages

def generate_markdown(metrics, packages):
    """Generate markdown table from coverage data."""
    markdown = "## 📊 Code Coverage Report\n\n"
    
    # Overall coverage table
    markdown += "### Overall Coverage\n\n"
    markdown += "| Metric | Coverage |\n"
    markdown += "|--------|----------|\n"
    
    # Display key metrics
    for metric_type in ['INSTRUCTION', 'BRANCH', 'LINE']:
        if metric_type in metrics:
            coverage = metrics[metric_type]['coverage']
            markdown += f"| {metric_type.capitalize()} | {coverage:.2f}% |\n"
    
    markdown += "\n"
    
    # Package-level coverage
    if packages:
        markdown += "### Coverage by Package\n\n"
        markdown += "| Package | Coverage |\n"
        markdown += "|---------|----------|\n"
        
        for pkg in packages:
            coverage = pkg['coverage']
            if coverage >= 80:
                emoji = "🟢"
            elif coverage >= 50:
                emoji = "🟡"
            else:
                emoji = "🔴"
            
            markdown += f"| {pkg['name']} | {emoji} {coverage:.2f}% |\n"
    
    return markdown

if __name__ == '__main__':
    xml_path = sys.argv[1] if len(sys.argv) > 1 else 'target/site/jacoco/jacoco.xml'
    metrics, packages = parse_jacoco_xml(xml_path)
    markdown = generate_markdown(metrics, packages)
    print(markdown)

