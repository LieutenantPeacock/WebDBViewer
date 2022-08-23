# syntax=docker/dockerfile:1

# Copyright (C) 2022  LieutenantPeacock
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

FROM eclipse-temurin:8-jdk-alpine as build
ARG WARFILE=target/*.war
COPY ${WARFILE} app.war
RUN mkdir build && (cd build; jar -xf ../app.war)
# Set user and group permission to r-- for all files
RUN chmod -R 440 build
# Set user and group permission to r-x for directories
RUN find build -type d -exec chmod 550 {} \;

FROM eclipse-temurin:8-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
# Set user permission to r-x and group permission to rwx (because program may need to create folders under /app)
RUN chown root:spring . && chmod 570 .
COPY --from=build --chown=:spring build/org org
COPY --from=build --chown=:spring build/META-INF META-INF
COPY --from=build --chown=:spring build/resources resources
COPY --from=build --chown=:spring build/WEB-INF WEB-INF
RUN chmod 550 *
USER spring:spring
ENTRYPOINT ["java", "org.springframework.boot.loader.WarLauncher"]