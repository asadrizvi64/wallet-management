# Troubleshooting Guide

## Local Development Issues

### Dev Server Error: "options.allowedHosts[0] should be a non-empty string"

**Cause**: Your local code is outdated. The project was recently migrated from `react-scripts` to Vite.

**Solution**:

1. **Resolve the package-lock.json conflict**:
   ```bash
   # Option 1: Discard your local changes (recommended if you haven't made intentional changes)
   cd frontend
   git checkout -- package-lock.json

   # Option 2: Stash your changes
   git stash
   ```

2. **Pull the latest changes**:
   ```bash
   git pull origin main
   ```

3. **Reinstall dependencies**:
   ```bash
   cd frontend
   npm install
   ```

4. **Start the development server**:
   ```bash
   npm start
   # This now runs Vite instead of react-scripts
   ```

### Alternative: Force Update (Clean State)

If you want to start fresh:

```bash
cd frontend
git fetch origin
git reset --hard origin/main
rm -rf node_modules package-lock.json
npm install
npm start
```

## CI/CD Issues

### Docker Login Error

**Cause**: Missing Docker Hub credentials in GitHub repository secrets.

**Solution**:

1. Go to your GitHub repository
2. Navigate to Settings → Secrets and variables → Actions
3. Add the following secrets:
   - `DOCKER_USERNAME`: Your Docker Hub username
   - `DOCKER_PASSWORD`: Your Docker Hub password or access token

**Note**: If you don't want to use Docker Hub, you can disable the docker-build job in `.github/workflows/ci-cd.yml` or the workflow will skip it automatically if secrets are not configured (after the fix).

### AWS Deployment Error

**Cause**: Missing AWS credentials (if you plan to deploy to AWS).

**Solution**:

If you want to use AWS deployment, add these secrets to GitHub:
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

If you don't need AWS deployment, the workflow will skip this step automatically (it only runs on main branch pushes).

## Common Issues

### Port Already in Use

If you see "Port 3000 is already in use":

```bash
# On Windows
netstat -ano | findstr :3000
taskkill /PID <PID> /F

# On Linux/Mac
lsof -i :3000
kill -9 <PID>
```

### Backend Connection Issues

The frontend proxies API requests to `http://localhost:8080`. Make sure your backend is running:

```bash
cd backend
mvn spring-boot:run
```

### Node Version Issues

This project requires Node.js 18+. Check your version:

```bash
node --version
```

If you need to upgrade, visit https://nodejs.org/
