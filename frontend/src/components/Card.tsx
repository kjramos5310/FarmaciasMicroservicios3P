import React from 'react';

interface CardProps {
  title: string;
  value: string | number;
  icon: string;
  color?: 'blue' | 'green' | 'red' | 'yellow';
  subtitle?: string;
}

const Card: React.FC<CardProps> = ({ title, value, icon, color = 'blue', subtitle }) => {
  const colorClasses = {
    blue: 'from-blue-500 to-blue-600',
    green: 'from-green-500 to-green-600',
    red: 'from-red-500 to-red-600',
    yellow: 'from-yellow-500 to-yellow-600',
  };

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow">
      <div className={`bg-gradient-to-r ${colorClasses[color]} p-4 text-white`}>
        <div className="flex items-center justify-between">
          <div className="flex-1">
            <p className="text-sm font-medium opacity-90">{title}</p>
            <p className="text-3xl font-bold mt-1">{value}</p>
            {subtitle && <p className="text-xs mt-1 opacity-75">{subtitle}</p>}
          </div>
          <div className="text-5xl opacity-80">{icon}</div>
        </div>
      </div>
    </div>
  );
};

export default Card;
