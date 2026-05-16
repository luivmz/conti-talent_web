/* =========================================================
   chart.js — Gráficos SVG personalizados (sin librerías)
   Soporta line/area chart con etiquetas y bar chart simple.
   ========================================================= */

const Chart = (() => {
  const NS = 'http://www.w3.org/2000/svg';
  const create = (tag, attrs = {}) => {
    const node = document.createElementNS(NS, tag);
    Object.entries(attrs).forEach(([k, v]) => node.setAttribute(k, v));
    return node;
  };

  /**
   * Renderiza un line chart estilo "Ventas durante los últimos 8 meses".
   * @param {HTMLElement} container
   * @param {{ label: string, puntos: Array<{mes: string, valor: number}>, color?: string }} dataset
   */
  const lineChart = (container, dataset) => {
    while (container.firstChild) container.removeChild(container.firstChild);

    const w = container.clientWidth || 700;
    const h = container.clientHeight || 280;
    const pad = { top: 30, right: 30, bottom: 36, left: 50 };

    const svg = create('svg', { viewBox: `0 0 ${w} ${h}`, width: '100%', height: '100%' });
    const points = dataset.puntos;
    if (!points.length) { container.appendChild(svg); return; }

    const valores = points.map((p) => p.valor);
    const minV = Math.min(...valores);
    const maxV = Math.max(...valores);
    const span = (maxV - minV) || 1;
    const yMin = Math.max(0, minV - span * 0.2);
    const yMax = maxV + span * 0.2;

    const xScale = (i) => pad.left + (i * (w - pad.left - pad.right)) / Math.max(points.length - 1, 1);
    const yScale = (v) => pad.top + ((yMax - v) * (h - pad.top - pad.bottom)) / (yMax - yMin || 1);

    // Grid horizontal (5 líneas)
    const gridLines = 5;
    for (let i = 0; i <= gridLines; i++) {
      const y = pad.top + ((h - pad.top - pad.bottom) * i) / gridLines;
      const value = Math.round(yMax - ((yMax - yMin) * i) / gridLines);
      svg.appendChild(create('line', { x1: pad.left, x2: w - pad.right, y1: y, y2: y, stroke: 'rgba(255,255,255,0.06)', 'stroke-width': 1 }));
      const tx = create('text', { x: pad.left - 10, y: y + 4, fill: '#6b6b78', 'font-size': 11, 'text-anchor': 'end', 'font-family': 'Inter, sans-serif' });
      tx.textContent = value;
      svg.appendChild(tx);
    }

    // Eje X labels
    points.forEach((p, i) => {
      const tx = create('text', { x: xScale(i), y: h - 14, fill: '#a8a8b3', 'font-size': 11, 'text-anchor': 'middle', 'font-family': 'Inter, sans-serif' });
      tx.textContent = p.mes;
      svg.appendChild(tx);
    });

    // Gradient defs
    const defs = create('defs');
    const grad = create('linearGradient', { id: 'chart-area', x1: '0', y1: '0', x2: '0', y2: '1' });
    grad.appendChild(create('stop', { offset: '0%',   'stop-color': dataset.color || '#6366f1', 'stop-opacity': '0.5' }));
    grad.appendChild(create('stop', { offset: '100%', 'stop-color': dataset.color || '#6366f1', 'stop-opacity': '0' }));
    defs.appendChild(grad);
    svg.appendChild(defs);

    // Área bajo la curva
    const areaPath = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${xScale(i)} ${yScale(p.valor)}`).join(' ')
      + ` L ${xScale(points.length - 1)} ${h - pad.bottom} L ${xScale(0)} ${h - pad.bottom} Z`;
    svg.appendChild(create('path', { d: areaPath, fill: 'url(#chart-area)' }));

    // Línea
    const linePath = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${xScale(i)} ${yScale(p.valor)}`).join(' ');
    svg.appendChild(create('path', { d: linePath, stroke: dataset.color || '#8b95ff', 'stroke-width': 2.5, fill: 'none', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }));

    // Puntos + valores
    points.forEach((p, i) => {
      const cx = xScale(i);
      const cy = yScale(p.valor);
      svg.appendChild(create('circle', { cx, cy, r: 4.5, fill: '#0d0d14', stroke: dataset.color || '#8b95ff', 'stroke-width': 2 }));
      const tx = create('text', { x: cx, y: cy - 12, fill: '#f5f5f7', 'font-size': 12, 'text-anchor': 'middle', 'font-family': 'Inter, sans-serif', 'font-weight': '600' });
      tx.textContent = p.valor;
      svg.appendChild(tx);
    });

    container.appendChild(svg);
  };

  /**
   * Renderiza un sparkline simple (sin ejes) para KPIs pequeños.
   */
  const sparkline = (container, points, color = '#8b95ff') => {
    while (container.firstChild) container.removeChild(container.firstChild);
    const w = container.clientWidth || 200;
    const h = container.clientHeight || 60;
    const valores = points.map((p) => p.valor || p);
    const minV = Math.min(...valores), maxV = Math.max(...valores);
    const span = (maxV - minV) || 1;
    const xScale = (i) => (i * w) / Math.max(valores.length - 1, 1);
    const yScale = (v) => h - ((v - minV) / span) * (h - 8) - 4;
    const svg = create('svg', { viewBox: `0 0 ${w} ${h}`, width: '100%', height: '100%' });
    const path = valores.map((v, i) => `${i === 0 ? 'M' : 'L'} ${xScale(i)} ${yScale(v)}`).join(' ');
    svg.appendChild(create('path', { d: path, stroke: color, fill: 'none', 'stroke-width': 2, 'stroke-linecap': 'round' }));
    container.appendChild(svg);
  };

  return { lineChart, sparkline };
})();

window.Chart = Chart;
