export const currentUser = {
  id: 4,
  name: 'Salma Bennani',
  username: 'salma.codes',
  email: 'salma@educhallenge.app',
  level: 'Level 8 Explorer',
  joinedAt: 'September 2025',
  school: 'Casablanca STEM Hub',
  bio: 'Curious learner who loves short coding sprints, math puzzles, and science challenges.',
  points: 2480,
  completedChallenges: 34,
  badges: 12,
  rank: 4,
  nextLevelPoints: 3000,
}

export const features = [
  {
    title: 'Create challenges',
    description:
      'Design interactive learning quests with clear rules, rewards, and deadlines.',
  },
  {
    title: 'Compete with others',
    description:
      'Join weekly competitions, compare scores, and stay motivated with friendly ranking.',
  },
  {
    title: 'Earn points and badges',
    description:
      'Turn progress into visible rewards that celebrate consistency and mastery.',
  },
  {
    title: 'Track progress',
    description:
      'Monitor your learning rhythm, streaks, and next milestones in one place.',
  },
]

export const leaderboard = [
  { id: 1, rank: 1, username: 'amina.math', points: 3820, level: 'Master', badge: 'Gold Crown' },
  { id: 2, rank: 2, username: 'youssef.ai', points: 3515, level: 'Champion', badge: 'Silver Spark' },
  { id: 3, rank: 3, username: 'nora.learns', points: 2980, level: 'Elite', badge: 'Focus Flame' },
  { id: 4, rank: 4, username: 'salma.codes', points: 2480, level: 'Explorer', badge: 'Orange Boost' },
  { id: 5, rank: 5, username: 'ilyas.quiz', points: 2255, level: 'Riser', badge: 'Quick Solver' },
  { id: 6, rank: 6, username: 'zineb.logic', points: 2100, level: 'Riser', badge: 'Momentum' },
]

export const challenges = [
  {
    id: 'algebra-blitz',
    title: 'Algebra Blitz',
    description: 'Solve rapid-fire algebra equations and unlock bonus points for perfect streaks.',
    difficulty: 'Medium',
    pointsReward: 240,
    category: 'Mathematics',
    duration: '20 min',
    participants: 146,
    status: 'Open now',
    steps: [
      'Warm up with linear equation drills.',
      'Complete the timed challenge set before the countdown ends.',
      'Review mistakes and earn a streak bonus on retry.',
    ],
  },
  {
    id: 'code-quest',
    title: 'Code Quest',
    description: 'Build small logic snippets and complete debugging tasks in JavaScript.',
    difficulty: 'Hard',
    pointsReward: 320,
    category: 'Programming',
    duration: '35 min',
    participants: 92,
    status: 'Trending',
    steps: [
      'Read the challenge brief and expected output.',
      'Fix the buggy functions and pass each hidden test.',
      'Submit your final implementation for extra mastery points.',
    ],
  },
  {
    id: 'science-sprint',
    title: 'Science Sprint',
    description: 'Answer quick concept questions on energy, forces, and scientific reasoning.',
    difficulty: 'Easy',
    pointsReward: 180,
    category: 'Science',
    duration: '15 min',
    participants: 204,
    status: 'Recommended',
    steps: [
      'Complete the concept cards in order.',
      'Answer the mini quiz after each topic.',
      'Unlock the bonus experiment scenario at the end.',
    ],
  },
  {
    id: 'history-hunt',
    title: 'History Hunt',
    description: 'Travel through key events and organize them on a gamified interactive timeline.',
    difficulty: 'Medium',
    pointsReward: 210,
    category: 'History',
    duration: '18 min',
    participants: 88,
    status: 'New',
    steps: [
      'Read each historical clue carefully.',
      'Place events in the correct chronological order.',
      'Finish with the critical thinking challenge.',
    ],
  },
  {
    id: 'english-debate-lab',
    title: 'English Debate Lab',
    description: 'Practice persuasive writing, vocabulary choice, and argument structure.',
    difficulty: 'Medium',
    pointsReward: 230,
    category: 'Languages',
    duration: '22 min',
    participants: 123,
    status: 'Open now',
    steps: [
      'Choose the debate prompt and identify your stance.',
      'Craft a short argument using the vocabulary bank.',
      'Complete the final language precision check.',
    ],
  },
  {
    id: 'data-detective',
    title: 'Data Detective',
    description: 'Interpret charts, compare datasets, and extract insights from dashboards.',
    difficulty: 'Hard',
    pointsReward: 340,
    category: 'Analytics',
    duration: '30 min',
    participants: 76,
    status: 'Top pick',
    steps: [
      'Inspect the visual dashboards and key metrics.',
      'Answer multi-step reasoning questions from the data.',
      'Summarize the best insights to secure bonus points.',
    ],
  },
]

export const recentChallenges = [
  { title: 'Code Quest', category: 'Programming', points: '+320 pts', progress: 'Completed 2h ago' },
  { title: 'Science Sprint', category: 'Science', points: '+180 pts', progress: 'Completed yesterday' },
  { title: 'History Hunt', category: 'History', points: '+210 pts', progress: 'In progress' },
]

export const quickActions = [
  {
    title: 'Start a new challenge',
    description: 'Explore fresh learning quests based on your level and interests.',
    link: '/challenges',
  },
  {
    title: 'Check the leaderboard',
    description: 'See how close you are to the next top spot this week.',
    link: '/leaderboard',
  },
  {
    title: 'Review your profile',
    description: 'Track badges, history, and level progress in one place.',
    link: '/profile',
  },
  {
    title: 'Create a challenge',
    description: 'Publish a new educational challenge and share it with other learners.',
    link: '/challenges/create',
  },
]

export const badgeCollection = [
  { title: 'Gold Crown', description: 'Reached the top 3 of the weekly leaderboard.', tone: 'gold' },
  { title: 'Focus Flame', description: 'Completed 10 challenges without missing a streak.', tone: 'silver' },
  { title: 'Quick Solver', description: 'Finished 5 timed quizzes before the bonus timer.', tone: 'bronze' },
  { title: 'Orange Boost', description: 'Earned 1,000 points in a single month.', tone: 'gold' },
  { title: 'Logic Builder', description: 'Solved advanced reasoning challenges.', tone: 'silver' },
  { title: 'Community Spark', description: 'Created and shared your first challenge.', tone: 'bronze' },
]

export const participationHistory = [
  { title: 'Algebra Blitz', date: 'April 16, 2026', outcome: 'Completed', score: '92%' },
  { title: 'English Debate Lab', date: 'April 14, 2026', outcome: 'Completed', score: '88%' },
  { title: 'Science Sprint', date: 'April 11, 2026', outcome: 'Won bonus', score: '96%' },
  { title: 'Data Detective', date: 'April 08, 2026', outcome: 'Completed', score: '84%' },
]

export const overviewStats = [
  { label: 'Total points', value: '2,480', hint: '+180 this week' },
  { label: 'Completed challenges', value: '34', hint: '4 active right now' },
  { label: 'Earned badges', value: '12', hint: '2 close to unlock' },
  { label: 'Current rank', value: '#4', hint: 'Need 175 pts for top 3' },
]
