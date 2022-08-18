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

FROM eclipse-temurin:8-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
COPY --from=build build/org org
COPY --from=build build/META-INF META-INF
COPY --from=build build/resources resources
COPY --from=build build/WEB-INF WEB-INF
ENTRYPOINT ["java", "org.springframework.boot.loader.WarLauncher"]