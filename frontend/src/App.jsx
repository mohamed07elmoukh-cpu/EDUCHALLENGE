import { Route, Routes } from 'react-router-dom'
import MainLayout from './layouts/MainLayout'
import ChallengeDetailsPage from './pages/ChallengeDetailsPage'
import CreateChallengePage from './pages/CreateChallengePage'
import AdminChallengesPage from './pages/AdminChallengesPage'
import ChallengesPage from './pages/ChallengesPage'
import DashboardPage from './pages/DashboardPage'
import HomePage from './pages/HomePage'
import LeaderboardPage from './pages/LeaderboardPage'
import LoginPage from './pages/LoginPage'
import MyChallengesPage from './pages/MyChallengesPage'
import NotFoundPage from './pages/NotFoundPage'
import ProfilePage from './pages/ProfilePage'
import RegisterPage from './pages/RegisterPage'

function App() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/admin/challenges" element={<AdminChallengesPage />} />
        <Route path="/challenges" element={<ChallengesPage />} />
        <Route path="/challenges/create" element={<CreateChallengePage />} />
        <Route path="/challenges/:challengeId" element={<ChallengeDetailsPage />} />
        <Route path="/my-challenges" element={<MyChallengesPage />} />
        <Route path="/leaderboard" element={<LeaderboardPage />} />
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  )
}

export default App
