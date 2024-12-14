# Use Node.js to build the Angular app
FROM node:20-alpine AS build

# Set the working directory
WORKDIR /app

# Copy package.json and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the application code and build it
COPY . .
RUN npm run build --prod

# Use NGINX to serve the Angular app
FROM nginx:alpine

# Copy the build output to the NGINX html folder
COPY --from=build /app/dist/elearning-frontend /usr/share/nginx/html

# Copy custom NGINX configuration for Angular routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 for the frontend service
EXPOSE 80

# Start NGINX
CMD ["nginx", "-g", "daemon off;"]
