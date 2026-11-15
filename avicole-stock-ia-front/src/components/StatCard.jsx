function StatCard({ title, value, icon }) {
  return (
    <article className="stat-card">
      <p className="stat-icon">{icon}</p>
      <p className="stat-title">{title}</p>
      <h3 className="stat-value">{value}</h3>
    </article>
  );
}

export default StatCard;
